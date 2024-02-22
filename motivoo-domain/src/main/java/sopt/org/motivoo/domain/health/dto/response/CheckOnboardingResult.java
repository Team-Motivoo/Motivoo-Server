package sopt.org.motivoo.domain.health.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CheckOnboardingResult(
        @JsonProperty("is_finished_onboarding")
        boolean isFinishedOnboarding
){
}
