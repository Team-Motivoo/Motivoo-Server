package sopt.org.motivooServer.domain.auth.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import sopt.org.motivooServer.domain.user.entity.User;

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

    public static LoginResponse of(Long userId, String nickname, String accessToken, String refreshToken) {
        return LoginResponse.builder()
            .id(userId.toString())
            .nickname(nickname)
            .tokenType("Bearer")
            .accessToken(accessToken)
            .refreshToken(refreshToken).build();
    }
}
