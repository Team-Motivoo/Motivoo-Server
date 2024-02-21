package sopt.org.motivoo.api.controller.auth.dto.redis;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshToken (
    Long id,

    @JsonProperty("refresh_token")
    String refreshToken
){

}
