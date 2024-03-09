package sopt.org.motivoo.api.controller.health.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.health.dto.response.OnboardingResult;

@Builder
public record OnboardingResponse (
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("exercise_level")
        String exerciseLevel
){

        public static OnboardingResponse of(final OnboardingResult result) {
                return OnboardingResponse.builder()
                        .userId(result.userId())
                        .exerciseLevel(result.exerciseLevel()).build();
        }
}
