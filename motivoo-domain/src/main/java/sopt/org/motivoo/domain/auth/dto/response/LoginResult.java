package sopt.org.motivoo.domain.auth.dto.response;

import lombok.Builder;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
public record LoginResult(
    String id,
    String nickname,
    String tokenType,

    String accessToken,
    String refreshToken,

    Boolean isFinishedOnboarding,
    Boolean isMatched
){

    private static final String BEARER_TYPE = "Bearer";

    public static LoginResult of(User user, String accessToken, String refreshToken, boolean isFinishedOnboarding, boolean isMatched) {
        return LoginResult.builder()
            .id(user.getSocialId())
            .nickname(user.getNickname())
            .tokenType(BEARER_TYPE)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .isFinishedOnboarding(isFinishedOnboarding)
            .isMatched(isMatched)
            .build();
    }

}
