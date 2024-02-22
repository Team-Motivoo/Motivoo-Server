package sopt.org.motivoo.domain.parentchild.service;

import static sopt.org.motivoo.common.response.SuccessType.*;
import static sopt.org.motivoo.domain.health.exception.HealthExceptionType.*;
import static sopt.org.motivoo.domain.parentchild.exception.ParentchildExceptionType.*;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.external.firebase.FirebaseService;
import sopt.org.motivoo.domain.external.slack.SlackService;
import sopt.org.motivoo.domain.health.dto.request.OnboardingCommand;
import sopt.org.motivoo.domain.health.dto.response.CheckOnboardingResult;
import sopt.org.motivoo.domain.health.dto.response.OnboardingResult;
import sopt.org.motivoo.domain.health.entity.ExerciseFrequency;
import sopt.org.motivoo.domain.health.entity.ExerciseLevel;
import sopt.org.motivoo.domain.health.entity.ExerciseTime;
import sopt.org.motivoo.domain.health.entity.ExerciseType;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.entity.HealthNote;
import sopt.org.motivoo.domain.health.exception.HealthException;
import sopt.org.motivoo.domain.health.repository.HealthRetriever;
import sopt.org.motivoo.domain.health.service.CalculateScore;
import sopt.org.motivoo.domain.parentchild.dto.request.InviteCommand;
import sopt.org.motivoo.domain.parentchild.dto.response.InviteResult;
import sopt.org.motivoo.domain.parentchild.dto.response.MatchingResult;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.parentchild.exception.ParentchildException;
import sopt.org.motivoo.domain.parentchild.repository.ParentchildRetriever;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.entity.UserType;
import sopt.org.motivoo.domain.user.repository.UserRetriever;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentchildService {

    private final UserRetriever userRetriever;
    private final HealthRetriever healthRetriever;
    private final ParentchildRetriever parentchildRetriever;

    private final CalculateScore calculateScore;
    private final FirebaseService firebaseService;
    private final SlackService slackService;

    private static final int RANDOM_STR_LEN = 8;
    private static final int MATCHING_SUCCESS = 2;

    @Transactional
    public OnboardingResult onboardInput(Long userId, OnboardingCommand request){
        User user = userRetriever.getUserById(userId);

        log.info("user="+user.getNickname()+"유무="+request.isExercise()+"타입="+request.exerciseType()
                +"횟수="+request.exerciseCount()+"시간="+request.exerciseTime()+"주의="+request.exerciseNote());

        if (healthRetriever.existsHealthByUser(user)) {   // 두번 API 호출하는 것을 막음
            throw new HealthException(EXIST_ONBOARDING_INFO);
        }

        user.updateOnboardingInfo(UserType.of(request.type()), request.age());

        Health health = Health.builder()
                .user(user)
                .isExercise(request.isExercise())
                .exerciseType(ExerciseType.of(request.exerciseType()))
                .exerciseFrequency(ExerciseFrequency.of(request.exerciseCount()))
                .exerciseTime(ExerciseTime.of(request.exerciseTime()))
                .healthNotes(HealthNote.of(request.exerciseNote()))
                .exerciseLevel(ExerciseLevel.BEGINNER).build();
        log.info("health user="+health.getId());

        //운동 특이사항 최대 3개까지 선택 가능
        if(health.getHealthNotes().size()>3)
            throw new HealthException(EXCEED_HEALTH_NOTES_RANGE);

        healthRetriever.save(health);

        // Slack에 신규 유저 가입 알림 전송
        try {
            slackService.sendSuccess(LOGIN_SUCCESS);
        } catch (IOException e) {
            log.error("슬랙 알림 전송에 실패했습니다.");
        }

        // 온보딩을 마친 유저의 걸음 수 데이터 DB에 추가
        /*try {
            firebaseService.insertUserStepById(userId);
        } catch (CannotCreateTransactionException e) {
            log.error("온보딩 입력 완료 후 FB에 데이터 추가 - 트랜잭션 처리 실패!");
        }*/

        double exerciseScore = calculateScore.calculate(request.isExercise(), ExerciseType.of(request.exerciseType()),
                ExerciseFrequency.of(request.exerciseCount()), ExerciseTime.of(request.exerciseTime()));
        log.info("가중치 결과 점수="+exerciseScore);
        health.updateExerciseLevel(exerciseScore);

        //초대 받는 입장
        if(user.getParentchild()!=null)
            return new OnboardingResult(userId, null, health.getExerciseLevel().getValue());

        String inviteCode = createInviteCode();

        Parentchild parentchild = Parentchild.builder()
                .inviteCode(inviteCode)
                .isMatched(false)
                .build();

        //초대 하는 입장
        user.addParentChild(parentchild);

        parentchildRetriever.saveParentchild(parentchild);
        return new OnboardingResult(userId, inviteCode, health.getExerciseLevel().getValue());
    }


    @Transactional
    public InviteResult validateInviteCode(Long userId, InviteCommand request){

        User user = userRetriever.getUserById(userId);

        // TODO 이미 매칭이 이루어진 경우에 대한 예외처리

        Parentchild parentchild = parentchildRetriever.getByInviteCode(request.inviteCode());

        // 잘못된 초대 코드를 입력하는 경우
        if (parentchild == null)
            return new InviteResult(userId, false, false, false);

        // 나의 매칭이 완료된 경우
        if(user.getParentchild()!=null && user.getParentchild().isMatched() == true)
            throw new ParentchildException(MATCH_ALREADY_COMPLETED);

        // 상대방이 이미 매칭이 완료된 경우
        if(userRetriever.getUserByParentchild(parentchild).size()>=2)
            throw new ParentchildException(MATCH_ALREADY_COMPLETED);

        // 부모-부모이거나 자녀-자녀인 경우
        if(user.getType() == userRetriever.getUserByParentchild(parentchild).get(0).getType())
            throw new ParentchildException(INVALID_PARENTCHILD_RELATION);


        if (!parentchild.isMatched()) {
            //1. 온보딩 정보 입력을 한 적이 있고 2. 내가 발급한 초대 코드인 경우
            if (healthRetriever.existsHealthByUser(user) && user.getParentchild() == parentchild)
                return new InviteResult(userId, false, true, true);
                //1. 온보딩 정보 입력을 한 적이 있고 2. 내가 발급한 초대 코드가 아닌 경우 [매칭 완료]
            else if (healthRetriever.existsHealthByUser(user) && user.getParentchild() != parentchild) {
                parentchild.matchingSuccess();
                user.addParentChild(parentchild);
                return new InviteResult(userId, true, false, true);
            }
            //1. 온보딩 정보 입력을 한 적이 없고 2. 내가 발급한 초대 코드가 아닌 경우 [매칭 완료]
            else if (!healthRetriever.existsHealthByUser(user) && user.getParentchild() != parentchild) {
                parentchild.matchingSuccess();
                user.addParentChild(parentchild);
                return new InviteResult(userId, true, false, false);
            }

            //매칭 완료
            parentchild.matchingSuccess();
            user.addParentChild(parentchild);
            return new InviteResult(userId, true, false, false); //[매칭 완료]
        }
        throw new ParentchildException(FAIL_TO_MATCH_PARENTCHILD);
    }

    public CheckOnboardingResult checkOnboardingInfo(Long userId){
        User user = userRetriever.getUserById(userId);

        if (healthRetriever.existsHealthByUser(user))
            return new CheckOnboardingResult(true);
        return new CheckOnboardingResult(false);
    }

    public MatchingResult checkMatching(Long userId){

        User user = userRetriever.getUserById(userId);

        if (user.getParentchild() != null) {
            int matchedCnt = userRetriever.getMatchedCnt(user.getParentchild());

            if(matchedCnt == MATCHING_SUCCESS) {
                Long opponentUserId = userRetriever.getOpponentUserId(user.getParentchild(), userId);
                return new MatchingResult(true, userId, opponentUserId);
            }

            // 초대 코드만 생성하고 아직 매칭에 성공하지 못한 경우
            throw new ParentchildException(MATCHING_NOT_FOUND);
        }

        // 부모-자식 관계가 없는 경우
        throw new ParentchildException(PARENTCHILD_NOT_FOUND);
    }

    public void checkForOneToOneMatch(List<User> users){
        if(users.size()==2){
            throw new ParentchildException(MATCH_ALREADY_COMPLETED);
        }
    }

    private String createInviteCode(){
        Random random = new Random();
        StringBuilder randomBuf = new StringBuilder();
        for (int i = 0; i < RANDOM_STR_LEN; i++) {
            int randomType = random.nextInt(3); // 0은 소문자, 1은 대문자, 2는 숫자를 나타냄

            switch (randomType) {
                case 0:
                    randomBuf.append((char) (random.nextInt(26) + 'a')); // 소문자 추가
                    break;
                case 1:
                    randomBuf.append((char) (random.nextInt(26) + 'A')); // 대문자 추가
                    break;
                case 2:
                    randomBuf.append(random.nextInt(10)); // 숫자 추가
                    break;
            }
        }
        return randomBuf.toString();
    }

}