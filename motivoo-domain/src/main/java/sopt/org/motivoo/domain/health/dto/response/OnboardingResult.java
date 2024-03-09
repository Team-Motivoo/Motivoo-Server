package sopt.org.motivoo.domain.health.dto.response;

import lombok.Builder;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
public record OnboardingResult(
        Long userId,
        String exerciseLevel
){

        public static OnboardingResult of(User user, Health health) {
                return OnboardingResult.builder()
                        .userId(user.getId())
                        .exerciseLevel(health.getExerciseLevel().getValue()).build();
        }
}
