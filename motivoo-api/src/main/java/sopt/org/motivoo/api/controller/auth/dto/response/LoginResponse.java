package sopt.org.motivoo.api.controller.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.auth.dto.response.LoginResult;

@Builder
public record LoginResponse (
        String id,
        String nickname,
        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("is_finished_onboarding")
        boolean isFinishedOnboarding,
        @JsonProperty("is_matched")
        boolean isMatched
){

    private static final String BEARER_TYPE = "Bearer";

    public static LoginResponse of(LoginResult result) {
        return LoginResponse.builder()
                .id(result.id())
                .nickname(result.nickname())
                .tokenType(result.tokenType())
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .isFinishedOnboarding(result.isFinishedOnboarding())
                .isMatched(result.isMatched()).build();
    }

}