package sopt.org.motivooServer.domain.health.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CheckOnboardingResponse (
        @JsonProperty("is_finished_onboarding")
        boolean isFinishedOnboarding
){
}
