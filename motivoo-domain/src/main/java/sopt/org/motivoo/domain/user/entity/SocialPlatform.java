package sopt.org.motivoo.domain.user.entity;

import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.user.exception.UserException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SocialPlatform {
	KAKAO("kakao"),
	APPLE("apple"),
	WITHDRAW("withdraw");

	private final String value;

	public static SocialPlatform of(String value) {
		return Arrays.stream(SocialPlatform.values())
				.filter(socialPlatform -> value.equals(socialPlatform.value))
				.findFirst()
				.orElseThrow(() -> new UserException(INVALID_SOCIAL_PLATFORM));
	}

}
