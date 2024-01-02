package sopt.org.motivooServer.domain.parentchild.exception;

import sopt.org.motivooServer.global.advice.BusinessException;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

public class ParentchildException extends BusinessException {

	public ParentchildException(BusinessExceptionType exceptionType) {
		super(exceptionType);
	}
}
