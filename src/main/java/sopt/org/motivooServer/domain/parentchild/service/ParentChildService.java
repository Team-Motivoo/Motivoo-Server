package sopt.org.motivooServer.domain.parentchild.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.motivooServer.domain.health.dto.request.OnboardingRequest;
import sopt.org.motivooServer.domain.health.dto.response.OnboardingResponse;
import sopt.org.motivooServer.domain.health.entity.Health;
import sopt.org.motivooServer.domain.health.exception.HealthException;
import sopt.org.motivooServer.domain.health.repository.HealthRepository;
import sopt.org.motivooServer.domain.health.service.CalculateScore;
import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;
import sopt.org.motivooServer.domain.parentchild.repository.ParentChildRepository;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.repository.UserRepository;

import java.util.Random;

import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.INVALID_USER_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentChildService {
    private final HealthRepository healthRepository;
    private final UserRepository userRepository;
    private final ParentChildRepository parentChildRepository;
    private final CalculateScore calculateScore;
    private static final int randomStrLen = 8;

    @Transactional
    public OnboardingResponse onboardInput(Long userId, OnboardingRequest request){
        User user = userRepository.findById(userId).orElseThrow(
            () -> new UserException(INVALID_USER_TYPE)
        );

        user.updateOnboardingInfo(request.type(), request.age());

        Health health = Health.builder()
                        .user(user)
                        .isExercise(request.isExercise())
                        .exerciseType(request.exerciseType())
                        .exerciseFrequency(request.exerciseCount())
                        .exerciseTime(request.exerciseTime())
                        .healthNotes(request.exerciseNote())
                        .build();

        healthRepository.save(health);

        String inviteCode = createInviteCode();

        Parentchild parentchild = Parentchild.builder()
                                  .inviteCode(inviteCode)
                                  .isMatched(false)
                                  .build();
        parentChildRepository.save(parentchild);

        double exerciseScore = calculateScore.calculate(request.isExercise(), request.exerciseType(),
                                              request.exerciseCount(), request.exerciseTime());

        health.updateExerciseLevel(exerciseScore);

        return new OnboardingResponse(userId, inviteCode, health.getExerciseLevel());
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
