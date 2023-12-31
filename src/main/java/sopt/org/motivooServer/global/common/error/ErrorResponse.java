package sopt.org.motivooServer.global.common.error;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
	HttpStatus status,
	String message
) {

	public static ErrorResponse of(ErrorType errorType) {
		return new ErrorResponse(
			errorType.getHttpStatus(),
			errorType.getMessage()
		);
	}
}
