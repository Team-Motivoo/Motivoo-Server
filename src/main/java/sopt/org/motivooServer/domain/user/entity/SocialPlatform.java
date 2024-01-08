package sopt.org.motivooServer.domain.user.entity;

import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.user.exception.UserException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SocialPlatform {

	KAKAO("카카오"),
	APPLE("애플");

	private final String value;

	public static SocialPlatform of(String value) {
		return Arrays.stream(SocialPlatform.values())
			.filter(socialPlatform -> value.equals(socialPlatform.value))
			.findFirst()
			.orElseThrow(() -> new UserException(INVALID_SOCIAL_PLATFORM));
	}


}
