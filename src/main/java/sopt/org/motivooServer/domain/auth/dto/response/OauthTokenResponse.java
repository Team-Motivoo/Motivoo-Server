package sopt.org.motivooServer.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


public record OauthTokenResponse (
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken
){
    public String getRefreshToken(){
        return this.refreshToken;
    }
}
