package sopt.org.motivoo.domain.parentchild.service;

import static sopt.org.motivoo.common.response.SuccessType.*;
import static sopt.org.motivoo.domain.parentchild.exception.ParentchildExceptionType.*;
import static sopt.org.motivoo.domain.parentchild.service.ParentchildManager.*;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.external.firebase.FirebaseService;
import sopt.org.motivoo.domain.external.slack.SlackService;
import sopt.org.motivoo.domain.health.dto.request.OnboardingCommand;
import sopt.org.motivoo.domain.health.dto.response.CheckOnboardingResult;
import sopt.org.motivoo.domain.health.dto.response.OnboardingResult;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.repository.HealthRetriever;
import sopt.org.motivoo.domain.parentchild.dto.request.InviteCommand;
import sopt.org.motivoo.domain.parentchild.dto.response.InviteReceiveResult;
import sopt.org.motivoo.domain.parentchild.dto.response.InviteSendResult;
import sopt.org.motivoo.domain.parentchild.dto.response.MatchingResult;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.parentchild.exception.ParentchildException;
import sopt.org.motivoo.domain.parentchild.repository.ParentchildRetriever;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.repository.UserRetriever;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentchildService {

    private final UserRetriever userRetriever;
    private final HealthRetriever healthRetriever;
    private final ParentchildRetriever parentchildRetriever;

    private final ParentchildManager parentchildManager;

    private final FirebaseService firebaseService;
    private final SlackService slackService;

    @Transactional
    public OnboardingResult onboardInput(Long userId, OnboardingCommand request){

        User user = userRetriever.getUserById(userId);
        Health health = parentchildManager.onboardInput(user, request);
        // healthRetriever.validateHealthByUser(user);   // TODO API 중복 호출 예외처리
        healthRetriever.save(health);

        // Slack 신규 유저 알림 전송
        sendSlackUserEntry();

        // TODO 온보딩을 마친 유저의 걸음 수 데이터 DB에 추가 (Firebase 연동)
        /*try {
            firebaseService.insertUserStepById(userId);
        } catch (CannotCreateTransactionException e) {
            log.error("온보딩 입력 완료 후 FB에 데이터 추가 - 트랜잭션 처리 실패!");
        }*/

        return OnboardingResult.of(user, health);
    }

    // Slack에 신규 유저 가입 알림 전송
    private void sendSlackUserEntry() {
        try {
            slackService.sendSuccess(LOGIN_SUCCESS);
        } catch (IOException e) {
            log.error("슬랙 알림 전송에 실패했습니다.");
        }
    }

    // 초대코드 입력을 통한 매칭
    @Transactional
    public InviteReceiveResult matchRelation(Long userId, InviteCommand request){

        User user = userRetriever.getUserById(userId);
        Parentchild parentchild = parentchildRetriever.getByInviteCode(request.inviteCode());

        // 이미 매칭이 완료된 경우에 대한 예외처리 -> TODO 양측에서 초대코드를 전송했을 때도 매칭이 가능해야 함
        validateInviteRequest(user, parentchild);

        if (!parentchild.isMatched()) {

            completeMatching(user, parentchild);
            Long opponentUserId = userRetriever.getOpponentUserId(parentchild, userId);

            return new InviteReceiveResult(userId, opponentUserId, true);
        }

        // 부모-부모이거나 자녀-자녀인 경우
        // if(user.getType() == userRetriever.getUserByParentchild(parentchild).get(0).getType())
        //     throw new ParentchildException(INVALID_PARENTCHILD_RELATION);

        throw new ParentchildException(FAIL_TO_MATCH_PARENTCHILD);
    }


    private void validateInviteRequest(User user, Parentchild parentchild) {
        int cntWithUser = userRetriever.getParentchildUserCnt(user.getParentchild());
        parentchildManager.validateUserRelation(user, cntWithUser);
        int cntWithInviteCode = userRetriever.getParentchildUserCnt(parentchild);
        parentchildManager.validateInviteCode(parentchild, cntWithInviteCode);
    }

    // 초대코드 발급 및 전송하기 (Parentchild 생성)
    @Transactional
    public InviteSendResult inviteMatchUser(final Long userId) {

        User user = userRetriever.getUserById(userId);

        // 매칭되지 않은 유저 - Parentchild 관계 존재
        if (existsParentchild(user)) {
            int matchedCnt = userRetriever.getParentchildUserCnt(user.getParentchild());
            if (user.getParentchild().validateParentchild(matchedCnt)) {
                return new InviteSendResult(userId, true, null);  // 매칭 완료 시 초대코드 null로 반환
            }

            return new InviteSendResult(userId, false, user.getParentchild().getInviteCode());
        }

        // 첫 발급 - 새로운 Parentchild 관계 생성
        Parentchild parentchild = parentchildManager.createParentchild(user);
        parentchildRetriever.saveParentchild(parentchild);
        return new InviteSendResult(userId, false, parentchild.getInviteCode());
    }

    private boolean existsParentchild(User user) {
        return user.getParentchild() != null;
    }

    public CheckOnboardingResult checkOnboardingInfo(Long userId){
        User user = userRetriever.getUserById(userId);

        if (healthRetriever.existsHealthByUser(user))
            return new CheckOnboardingResult(true);
        return new CheckOnboardingResult(false);
    }

    public MatchingResult checkMatching(Long userId){

        User user = userRetriever.getUserById(userId);

        if (!existsParentchild(user)) {
            // 부모-자식 관계가 없는 경우
            throw new ParentchildException(PARENTCHILD_NOT_FOUND);
        }

        int matchedCnt = userRetriever.getParentchildUserCnt(user.getParentchild());
        if (user.getParentchild().validateParentchild(matchedCnt)) {
            Long opponentUserId = userRetriever.getOpponentUserId(user.getParentchild(), userId);
            return new MatchingResult(true, userId, opponentUserId);
        }

        // 초대 코드만 생성하고 아직 매칭에 성공하지 못한 경우
        throw new ParentchildException(MATCHING_NOT_FOUND);
    }
}