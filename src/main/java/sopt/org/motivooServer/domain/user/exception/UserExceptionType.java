package sopt.org.motivooServer.domain.user.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserExceptionType implements BusinessExceptionType {

	/**
	 * 400 Bad Request
	 */
	INVALID_USER_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 회원 유형입니다."),

	INVALID_SOCIAL_PLATFORM(HttpStatus.BAD_REQUEST, "유효하지 않은 소셜 플랫폼입니다."),

	/**
	 * 404 Not Found
	 */
	TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰이 없습니다."),
	TOKEN_UNSUPPORTED(HttpStatus.NOT_FOUND, "변조된 토큰입니다."),

	/**
	 * 401 UNAUTHORIZED
	 */
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),



	;

	private final HttpStatus status;
	private final String message;

	@Override
	public HttpStatus status() {
		return this.status;
	}

	@Override
	public String message() {
		return this.message;
	}
}
