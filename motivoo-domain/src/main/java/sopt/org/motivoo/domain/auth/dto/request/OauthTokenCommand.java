package sopt.org.motivoo.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;


public record OauthTokenCommand(
    @JsonProperty("social_access_token")
    String accessToken,
    @JsonProperty("token_type")
    String tokenType
){

}
