package sopt.org.motivoo.domain.user.entity;

import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.user.exception.UserException;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserType {
	PARENT("부모"),
	CHILD("자녀"),
	NONE("없음")
	;

	private final String value;

	@JsonCreator
	public static UserType of(String value) {
		return Arrays.stream(UserType.values())
			.filter(userType -> value.equals(userType.value))
			.findFirst()
			.orElseThrow(() -> new UserException(INVALID_USER_TYPE));
	}
}
