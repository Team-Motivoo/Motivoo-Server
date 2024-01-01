package sopt.org.motivooServer.global.common.error;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record ErrorResponse(
	int code,
	String message,
	boolean success
) {

	public static ErrorResponse of(ErrorType errorType) {
		return new ErrorResponse(
			errorType.getHttpStatusCode(),
			errorType.getMessage(),
			false
		);
	}

	public static ErrorResponse of(ErrorType errorType, String message) {
		return new ErrorResponse(
			errorType.getHttpStatusCode(),
			message,
			false
		);
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum ErrorType {

		/**
		 * 500 Internal Server Error
		 */
		INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다"),

		;

		private final HttpStatus httpStatus;
		private final String message;

		public int getHttpStatusCode() {
			return httpStatus.value();
		}
	}
}
