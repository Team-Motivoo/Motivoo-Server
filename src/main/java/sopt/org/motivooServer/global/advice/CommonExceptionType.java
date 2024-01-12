package sopt.org.motivooServer.global.advice;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommonExceptionType implements BusinessExceptionType{
	/**
	 * 400 Bad Request
	 */
	REQUEST_VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "형식적 유효성 검사에 올바르지 않는 요청 값입니다"),

	/**
	 * 401 Unauthorized
	 */
	FAIL_TO_AUTHENTICATE_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "내부 인증 필터를 거치는 데 실패한 액세스 토큰입니다"),
	TOKEN_NOT_CONTAINS_USER_ID(HttpStatus.UNAUTHORIZED, "리프레시 토큰은 유저 아이디를 담고있지 않습니다."),

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