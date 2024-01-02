package sopt.org.motivooServer.domain.step.exception;

import sopt.org.motivooServer.global.advice.BusinessException;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

public class StepException extends BusinessException {

	public StepException(BusinessExceptionType exceptionType) {
		super(exceptionType);
	}
}
