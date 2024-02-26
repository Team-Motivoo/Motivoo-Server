package sopt.org.motivoo.api.controller.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.auth.dto.request.OauthTokenCommand;

public record OauthTokenRequest (
    @JsonProperty("social_access_token")
    String accessToken,
    @JsonProperty("token_type")
    String tokenType
){

    public OauthTokenCommand toServiceDto() {
        return new OauthTokenCommand(accessToken, tokenType);
    }

}
