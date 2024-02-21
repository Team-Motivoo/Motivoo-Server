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

	INVALID_APPLE_PUBLIC_KEY(HttpStatus.BAD_REQUEST, "Apple JWT 값의 alg, kid 정보가 올바르지 않습니다."),
	INVALID_APPLE_IDENTITY_TOKEN(HttpStatus.BAD_REQUEST, "Apple OAuth Identity Token 형식이 올바르지 않습니다."),
	EXPIRED_APPLE_IDENTITY_TOKEN(HttpStatus.BAD_REQUEST, "Apple OAuth 로그인 중 Identity Token 유효기간이 만료됐습니다."),
	INVALID_APPLE_CLAIMS(HttpStatus.BAD_REQUEST, "Apple OAuth Claims 값이 올바르지 않습니다."),
	INVALID_ENCRYPT_COMMUNICATION(HttpStatus.BAD_REQUEST, "Apple OAuth 통신 암호화 과정 중 문제가 발생했습니다."),
	CREATE_PUBLIC_KEY_EXCEPTION(HttpStatus.BAD_REQUEST, "Apple OAuth 로그인 중 public verify 생성에 문제가 발생했습니다."),

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