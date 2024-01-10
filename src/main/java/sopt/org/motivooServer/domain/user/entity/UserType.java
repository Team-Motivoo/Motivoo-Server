package sopt.org.motivooServer.domain.user.entity;

import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.user.exception.UserException;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserType {
	PARENT("부모"),
	CHILD("자녀"),
	NONE("없음")
	;

	private final String value;

	public static UserType of(String value) {
		return Arrays.stream(UserType.values())
			.filter(userType -> value.equals(userType.value))
			.findFirst()
			.orElseThrow(() -> new UserException(INVALID_USER_TYPE));
	}
}
