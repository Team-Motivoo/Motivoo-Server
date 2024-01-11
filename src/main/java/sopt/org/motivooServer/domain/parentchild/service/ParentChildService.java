package sopt.org.motivooServer.domain.parentchild.service;

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
import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;
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
                                  .isMatched(false)
                                  .build();
        parentchildRepository.save(parentchild);

        return new OnboardingResponse(userId, inviteCode, health.getExerciseLevel().getValue());
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
