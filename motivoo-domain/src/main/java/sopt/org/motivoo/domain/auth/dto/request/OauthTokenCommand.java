package sopt.org.motivooServer.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;


public record OauthTokenRequest (
    @JsonProperty("social_access_token")
    String accessToken,
    @JsonProperty("token_type")
    String tokenType
){

}
