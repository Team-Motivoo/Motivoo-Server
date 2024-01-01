package sopt.org.motivooServer.global.advice;

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
}
