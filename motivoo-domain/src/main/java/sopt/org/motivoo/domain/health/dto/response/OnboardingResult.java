package sopt.org.motivoo.domain.health.dto.response;

import lombok.Builder;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
public record OnboardingResult(
        Long userId,
        String inviteCode,
        String exerciseLevel
){

        public static OnboardingResult of(User user, Parentchild parentchild, Health health) {
                return OnboardingResult.builder()
                        .userId(user.getId())
                        .inviteCode(parentchild.getInviteCode())
                        .exerciseLevel(health.getExerciseLevel().getValue()).build();
        }

        public static OnboardingResult of(User user, Health health) {
                return OnboardingResult.builder()
                        .userId(user.getId())
                        .exerciseLevel(health.getExerciseLevel().getValue()).build();
        }
}
