package sopt.org.motivooServer.domain.parentchild.service;

import static sopt.org.motivooServer.domain.parentchild.exception.ParentchildExceptionType.*;
import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;

import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.health.dto.request.OnboardingRequest;
import sopt.org.motivooServer.domain.health.dto.response.OnboardingResponse;
import sopt.org.motivooServer.domain.health.entity.ExerciseFrequency;
import sopt.org.motivooServer.domain.health.entity.ExerciseLevel;
import sopt.org.motivooServer.domain.health.entity.ExerciseTime;
import sopt.org.motivooServer.domain.health.entity.ExerciseType;
import sopt.org.motivooServer.domain.health.entity.Health;
import sopt.org.motivooServer.domain.health.entity.HealthNote;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentChildService {
    private final HealthRepository healthRepository;
    private final UserRepository userRepository;
    private final ParentchildRepository parentchildRepository;
    private final CalculateScore calculateScore;
    private static final int randomStrLen = 8;
    private static final int matchingSuccess = 2;

    @Transactional
    public OnboardingResponse onboardInput(Long userId, OnboardingRequest request){
        User user = userRepository.findById(userId).orElseThrow(
            () -> new UserException(INVALID_USER_TYPE)
        );

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

        healthRepository.save(health);

        double exerciseScore = calculateScore.calculate(request.isExercise(), ExerciseType.of(request.exerciseType()),
                ExerciseFrequency.of(request.exerciseCount()), ExerciseTime.of(request.exerciseTime()));
        health.updateExerciseLevel(exerciseScore);

        String inviteCode = createInviteCode();

        Parentchild parentchild = Parentchild.builder()
            .inviteCode(inviteCode)
            .isMatched(false).build();
        parentchildRepository.save(parentchild);
        user.addParentChild(parentchild);

        return new OnboardingResponse(userId, inviteCode, health.getExerciseLevel().getValue());
    }

    @Transactional
    public InviteResponse validateInviteCode(Long userId, InviteRequest request){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(INVALID_USER_TYPE)
        );

        Parentchild parentchild = parentchildRepository.findByInviteCode(request.inviteCode());
        if(parentchild!=null){
            checkForOneToOneMatch(parentchild); //이미 매칭이 완료된 경우 예외처리
            parentchild.matchingSuccess();
            user.addParentChild(parentchild);
            return new InviteResponse(userId, true);
        }
        return new InviteResponse(userId, false);
    }

    public MatchingResponse checkMatching(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(INVALID_USER_TYPE)
        );
        if(user.getParentchild()!=null){
            int matcedCnt = userRepository.countByParentchild(user.getParentchild());
            log.info("매칭된 숫자="+matcedCnt);
            if(matcedCnt == matchingSuccess) {
                Long opponentUserId = userRepository.getOpponentId(user.getParentchild(), userId);
                log.info("상대편 유저 아이디="+opponentUserId);
                return new MatchingResponse(true, userId, opponentUserId);
            }
            //초대 코드만 생성하고 아직 매칭에 성공하지 못한 경우
            throw new ParentchildException(MATCHING_NOT_FOUND);
        }
        //부모-자식 관계까 없는 경우
        throw new ParentchildException(PARENTCHILD_NOT_FOUND);
    }

    public void checkForOneToOneMatch(Parentchild parentchild){
        if(parentchild.getIsMatched()){
            throw new ParentchildException(MATCH_ALREADY_COMPLETED);
        }
    }
    private String createInviteCode(){
        Random random = new Random();
        StringBuilder randomBuf = new StringBuilder();
        for (int i = 0; i < randomStrLen; i++) {
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
