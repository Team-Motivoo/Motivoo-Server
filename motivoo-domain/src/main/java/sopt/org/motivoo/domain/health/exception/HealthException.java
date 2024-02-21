package sopt.org.motivoo.domain.health.exception;

import sopt.org.motivoo.common.advice.BusinessException;
import sopt.org.motivoo.common.advice.BusinessExceptionType;

public class HealthException extends BusinessException {

	public HealthException(BusinessExceptionType exceptionType) {
		super(exceptionType);
	}
}
