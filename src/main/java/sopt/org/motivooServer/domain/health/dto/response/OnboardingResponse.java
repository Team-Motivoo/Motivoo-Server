package sopt.org.motivooServer.domain.health.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OnboardingResponse (
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("invite_code")
        String inviteCode,
        @JsonProperty("exercise_level")
        String exerciseLevel
){

}
