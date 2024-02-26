package sopt.org.motivoo.domain.parentchild.exception;

import sopt.org.motivoo.common.advice.BusinessException;
import sopt.org.motivoo.common.advice.BusinessExceptionType;

public class ParentchildException extends BusinessException {

	public ParentchildException(BusinessExceptionType exceptionType) {
		super(exceptionType);
	}
}
