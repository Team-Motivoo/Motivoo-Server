package sopt.org.motivooServer.domain.user.exception;

import sopt.org.motivooServer.global.advice.BusinessException;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

public class UserException extends BusinessException {

	public UserException(BusinessExceptionType exceptionType) {
		super(exceptionType);
	}
}
