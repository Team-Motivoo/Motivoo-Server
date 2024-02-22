package sopt.org.motivoo.common.advice;

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

	public static ErrorResponse of(BusinessExceptionType exceptionType) {
		return new ErrorResponse(
			exceptionType.getHttpStatusCode(),
			exceptionType.message(),
			false
		);
	}
}
