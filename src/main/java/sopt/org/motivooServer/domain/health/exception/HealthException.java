package sopt.org.motivooServer.domain.health.exception;

import sopt.org.motivooServer.global.advice.BusinessException;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

public class HealthException extends BusinessException {

	public HealthException(BusinessExceptionType exceptionType) {
		super(exceptionType);
	}
}
