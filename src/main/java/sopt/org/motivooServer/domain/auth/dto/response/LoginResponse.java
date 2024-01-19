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

    private static final String BEARER_TYPE = "Bearer";

    public static LoginResponse of(User user, String accessToken, String refreshToken) {
        return LoginResponse.builder()
            .id(user.getSocialId())
            .nickname(user.getNickname())
            .tokenType(BEARER_TYPE)
            .accessToken(accessToken)
            .refreshToken(refreshToken).build();
    }

}
