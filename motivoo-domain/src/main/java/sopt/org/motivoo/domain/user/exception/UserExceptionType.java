package sopt.org.motivoo.domain.user.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.common.advice.BusinessExceptionType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserExceptionType implements BusinessExceptionType {

	/**
	 * 400 Bad Request
	 */
	INVALID_USER_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 회원 유형입니다."),
	INVALID_SOCIAL_PLATFORM(HttpStatus.BAD_REQUEST, "유효하지 않은 소셜 플랫폼입니다."),
	NULL_VALUE_USERTYPE_ENUM(HttpStatus.BAD_REQUEST, "데이터베이스의 유저 타입이 유효하지 않은 값입니다."),
	NULL_VALUE_AGE(HttpStatus.BAD_REQUEST, "유저의 나이는 null이어서는 안 됩니다."),
	ALREADY_WITHDRAW_USER(HttpStatus.BAD_REQUEST, "이미 탈퇴한 유저입니다."),

	/**
	 * 401 Unauthorized
	 */
	EMPTY_PRINCIPLE_EXCEPTION(HttpStatus.UNAUTHORIZED, "액세스 토큰이 비어있거나, 유효하지 않은 엑세스 토큰입니다."),
	TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "변조된 토큰입니다."),
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
	TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰을 찾을 수 없습니다."),


	/**
	 * 404 Not Found
	 */
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

	/**
	 * 412 Precondition Failed
	 */
	ALREADY_WITHDRAW_OPPONENT_USER(HttpStatus.PRECONDITION_FAILED, "상대 유저가 탈퇴하여 요청을 처리할 수 없습니다."),

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
