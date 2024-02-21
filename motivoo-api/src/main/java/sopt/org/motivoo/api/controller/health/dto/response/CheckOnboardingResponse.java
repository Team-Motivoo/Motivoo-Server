package sopt.org.motivoo.api.controller.health.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.health.dto.response.CheckOnboardingResult;

@Builder
public record CheckOnboardingResponse (
        @JsonProperty("is_finished_onboarding")
        boolean isFinishedOnboarding
){

        public static CheckOnboardingResponse of(final CheckOnboardingResult result) {
                return CheckOnboardingResponse.builder()
                        .isFinishedOnboarding(result.isFinishedOnboarding()).build();
        }
}
