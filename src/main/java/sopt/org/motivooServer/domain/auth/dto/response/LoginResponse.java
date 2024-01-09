package sopt.org.motivooServer.domain.auth.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record LoginResponse (
    String id,
    String nickname,
    @JsonProperty("token_type")
    String tokenType,

    @JsonProperty("access_token")
    String accessToken,
    @JsonProperty("refresh_token")
    String refreshToken
){
}
