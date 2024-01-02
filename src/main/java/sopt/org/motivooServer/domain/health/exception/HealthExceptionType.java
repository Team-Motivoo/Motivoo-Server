package sopt.org.motivooServer.domain.health.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum HealthExceptionType implements BusinessExceptionType {

	/**
	 * 400 Bad Request
	 */
	INVALID_HEALTH_NOTE(HttpStatus.BAD_REQUEST, "유효하지 않는 건강 주의사항입니다."),

	/**
	 * 404 Not Found
	 */
	HEALTH_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 건강정보입니다.")


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
