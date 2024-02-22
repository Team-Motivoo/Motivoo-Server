package sopt.org.motivoo.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
public record LoginResult(
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

    public static LoginResult of(User user, String accessToken, String refreshToken) {
        return LoginResult.builder()
            .id(user.getSocialId())
            .nickname(user.getNickname())
            .tokenType(BEARER_TYPE)
            .accessToken(accessToken)
            .refreshToken(refreshToken).build();
    }

}
