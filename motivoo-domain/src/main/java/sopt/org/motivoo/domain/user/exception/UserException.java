package sopt.org.motivoo.domain.user.exception;

import sopt.org.motivoo.common.advice.BusinessException;
import sopt.org.motivoo.common.advice.BusinessExceptionType;

public class UserException extends BusinessException {

	public UserException(BusinessExceptionType exceptionType) {
		super(exceptionType);
	}
}
