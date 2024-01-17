package sopt.org.motivooServer.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OauthTokenResponse (
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken
){
}
