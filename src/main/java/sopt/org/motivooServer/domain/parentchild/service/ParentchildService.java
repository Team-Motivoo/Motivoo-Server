package sopt.org.motivooServer.domain.parentchild.service;


import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.EXCEED_HEALTH_NOTES_RANGE;
import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.EXIST_ONBOARDING_INFO;
import static sopt.org.motivooServer.domain.parentchild.exception.ParentchildExceptionType.*;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.health.dto.request.OnboardingRequest;
import sopt.org.motivooServer.domain.health.dto.response.CheckOnboardingResponse;
import sopt.org.motivooServer.domain.health.dto.response.OnboardingResponse;
import sopt.org.motivooServer.domain.health.entity.ExerciseFrequency;
import sopt.org.motivooServer.domain.health.entity.ExerciseLevel;
import sopt.org.motivooServer.domain.health.entity.ExerciseTime;
import sopt.org.motivooServer.domain.health.entity.ExerciseType;
import sopt.org.motivooServer.domain.health.entity.Health;
import sopt.org.motivooServer.domain.health.entity.HealthNote;
import sopt.org.motivooServer.domain.health.exception.HealthException;
import sopt.org.motivooServer.domain.health.repository.HealthRepository;
import sopt.org.motivooServer.domain.health.service.CalculateScore;
import sopt.org.motivooServer.domain.parentchild.dto.request.InviteRequest;
import sopt.org.motivooServer.domain.parentchild.dto.response.InviteResponse;
import sopt.org.motivooServer.domain.parentchild.dto.response.MatchingResponse;
import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;
import sopt.org.motivooServer.domain.parentchild.exception.ParentchildException;
import sopt.org.motivooServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.entity.UserType;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.repository.UserRepository;
import sopt.org.motivooServer.global.external.firebase.FirebaseService;
import sopt.org.motivooServer.global.external.slack.SlackService;

import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.INVALID_USER_TYPE;
import static sopt.org.motivooServer.global.response.SuccessType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentchildService {
    private final HealthRepository healthRepository;
    private final UserRepository userRepository;
    private final ParentchildRepository parentchildRepository;
    private final CalculateScore calculateScore;
    private final FirebaseService firebaseService;
    private final SlackService slackService;

    private static final int RANDOM_STR_LEN = 8;
    private static final int MATCHING_SUCCESS = 2;

    @Transactional
    public OnboardingResponse onboardInput(Long userId, OnboardingRequest request){
        User user = getUserById(userId);

        log.info("user="+user.getNickname()+"유무="+request.isExercise()+"타입="+request.exerciseType()
                +"횟수="+request.exerciseCount()+"시간="+request.exerciseTime()+"주의="+request.exerciseNote());

        if(!healthRepository.findByUser(user).isEmpty()) //두번 API 호출하는 것을 막음
            throw new HealthException(EXIST_ONBOARDING_INFO);

        user.updateOnboardingInfo(UserType.of(request.type()), request.age());

        Health health = Health.builder()
                .user(user)
                .isExercise(request.isExercise())
                .exerciseType(ExerciseType.of(request.exerciseType()))
                .exerciseFrequency(ExerciseFrequency.of(request.exerciseCount()))
                .exerciseTime(ExerciseTime.of(request.exerciseTime()))
                .healthNotes(HealthNote.of(request.exerciseNote()))
                .exerciseLevel(ExerciseLevel.BEGINNER)
                .build();
        log.info("health user="+health.getId());

        //운동 특이사항 최대 3개까지 선택 가능
        if(health.getHealthNotes().size()>3)
            throw new HealthException(EXCEED_HEALTH_NOTES_RANGE);

        healthRepository.save(health);

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
            return new OnboardingResponse(userId, null, health.getExerciseLevel().getValue());

        String inviteCode = createInviteCode();

        Parentchild parentchild = Parentchild.builder()
                .inviteCode(inviteCode)
                .isMatched(false)
                .build();

        //초대 하는 입장
        user.addParentChild(parentchild);

        parentchildRepository.save(parentchild);
        return new OnboardingResponse(userId, inviteCode, health.getExerciseLevel().getValue());
    }


    @Transactional
    public InviteResponse validateInviteCode(Long userId, InviteRequest request){
        User user = getUserById(userId);
//        //매칭 성공시 API 두 번 호출하는 것을 막음
//        if(user.getParentchild()!=null){
//            throw new ParentchildException(INVALID_PARENTCHILD_RELATION);
//        }

        Parentchild parentchild = parentchildRepository.findByInviteCode(request.inviteCode());

        log.info("parentchild="+parentchild);
        //잘못된 초대 코드를 입력하는 경우
        if(parentchild == null)
            return new InviteResponse(userId, false, false, false);

        //나의 매칭이 완료된 경우
        if(user.getParentchild()!=null && user.getParentchild().isMatched() == true)
            throw new ParentchildException(MATCH_ALREADY_COMPLETED);

        //상대방이 이미 매칭이 완료된 경우
        if(userRepository.findUserByParentchild(parentchild).size()>=2)
            throw new ParentchildException(MATCH_ALREADY_COMPLETED);

        //부모-부모이거나 자녀-자녀인 경우
        if(user.getType() == userRepository.findUserByParentchild(parentchild).get(0).getType())
            throw new ParentchildException(INVALID_PARENTCHILD_RELATION);


        if (!parentchild.isMatched()) {
            //1. 온보딩 정보 입력을 한 적이 있고 2. 내가 발급한 초대 코드인 경우
            if (!healthRepository.findByUser(user).isEmpty() && user.getParentchild() == parentchild)
                return new InviteResponse(userId, false, true, true);
                //1. 온보딩 정보 입력을 한 적이 있고 2. 내가 발급한 초대 코드가 아닌 경우 [매칭 완료]
            else if (!healthRepository.findByUser(user).isEmpty() && user.getParentchild() != parentchild) {
                parentchild.matchingSuccess();
                user.addParentChild(parentchild);
                return new InviteResponse(userId, true, false, true);
            }
            //1. 온보딩 정보 입력을 한 적이 없고 2. 내가 발급한 초대 코드가 아닌 경우 [매칭 완료]
            else if (healthRepository.findByUser(user).isEmpty() && user.getParentchild() != parentchild) {
                parentchild.matchingSuccess();
                user.addParentChild(parentchild);
                return new InviteResponse(userId, true, false, false);
            }

            //매칭 완료
            parentchild.matchingSuccess();
            user.addParentChild(parentchild);
            return new InviteResponse(userId, true, false, false); //[매칭 완료]
        }
        throw new ParentchildException(FAIL_TO_MATCH_PARENTCHILD);
    }

    public CheckOnboardingResponse checkOnboardingInfo(Long userId){
        User user = getUserById(userId);

        if(!healthRepository.findByUser(user).isEmpty())
            return new CheckOnboardingResponse(true);
        return new CheckOnboardingResponse(false);
    }

    public MatchingResponse checkMatching(Long userId){
        User user = getUserById(userId);
        if(user.getParentchild()!=null){
            int matcedCnt = userRepository.countByParentchild(user.getParentchild());
            log.info("매칭된 숫자="+matcedCnt);
            if(matcedCnt == MATCHING_SUCCESS) {
                Long opponentUserId = userRepository.getOpponentId(user.getParentchild(), userId);
                log.info("상대편 유저 아이디="+opponentUserId);
                return new MatchingResponse(true, userId, opponentUserId);
            }
            //초대 코드만 생성하고 아직 매칭에 성공하지 못한 경우
            throw new ParentchildException(MATCHING_NOT_FOUND);
        }
        //부모-자식 관계가 없는 경우
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

    private User getUserById(Long userId) {
        log.info("유저 아이디="+userId);
        return userRepository.findById(userId).orElseThrow(
                () -> new UserException(INVALID_USER_TYPE)
        );
    }
}
