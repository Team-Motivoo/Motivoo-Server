package sopt.org.motivoo.domain.health.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
public record OnboardingResult(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("invite_code")
        String inviteCode,
        @JsonProperty("exercise_level")
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
