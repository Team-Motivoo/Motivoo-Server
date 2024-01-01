package sopt.org.motivooServer.global.common.exception;

import lombok.Getter;
import sopt.org.motivooServer.global.common.error.ErrorResponse;

@Getter
public class BusinessException extends RuntimeException {

	private final ErrorResponse.ErrorType errorType;

	public BusinessException(ErrorResponse.ErrorType errorType) {
		super(errorType.getMessage());
		this.errorType = errorType;
	}

	public int getHttpStatus() {
		return errorType.getHttpStatusCode();
	}
}