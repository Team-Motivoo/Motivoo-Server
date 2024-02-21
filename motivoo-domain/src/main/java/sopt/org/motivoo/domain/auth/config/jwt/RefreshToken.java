package sopt.org.motivoo.domain.auth.config.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshToken (
    Long id,

    @JsonProperty("refresh_token")
    String refreshToken
){

}
