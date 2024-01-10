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
	UNSUPPORTED_IMAGE_EXTENSION(HttpStatus.BAD_REQUEST, "이미지 확장자는 jpg, png, webp만 가능합니다."),
	UNSUPPORTED_IMAGE_SIZE(HttpStatus.BAD_REQUEST, "이미지 사이즈는 5MB를 넘을 수 없습니다."),

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