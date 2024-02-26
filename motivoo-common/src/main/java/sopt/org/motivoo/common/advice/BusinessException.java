package sopt.org.motivooServer.global.advice;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private final BusinessExceptionType exceptionType;

	public BusinessException(BusinessExceptionType exceptionType) {
		super(exceptionType.message());
		this.exceptionType = exceptionType;
	}

	public BusinessException(String message, BusinessExceptionType exceptionType) {
		super(message);
		this.exceptionType = exceptionType;
	}

	public int getHttpStatus() {
		return exceptionType.status().value();
	}
}