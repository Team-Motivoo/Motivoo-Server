package sopt.org.motivoo.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OauthTokenResult(
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken
){
    public String getRefreshToken(){
        return this.refreshToken;
    }
}
