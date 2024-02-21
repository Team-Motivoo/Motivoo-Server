package sopt.org.motivoo.api.controller.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.auth.dto.response.OauthTokenResult;

public record OauthTokenResponse (
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken
){
    public String getRefreshToken(){
        return this.refreshToken;
    }

    public static OauthTokenResponse of(OauthTokenResult result) {
        return new OauthTokenResponse(result.accessToken(), result.refreshToken());
    }
}
