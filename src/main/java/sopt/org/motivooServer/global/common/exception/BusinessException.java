package sopt.org.motivooServer.global.common.exception;

import lombok.Getter;
import sopt.org.motivooServer.global.common.error.ErrorType;

@Getter
public class BusinessException extends RuntimeException {

	private final ErrorType errorType;

	public BusinessException(ErrorType errorType) {
		super(errorType.getMessage());
		this.errorType = errorType;
	}

	public int getHttpStatus() {
		return errorType.getHttpStatusCode();
	}
}