package sopt.org.motivooServer.domain.user.exception;

import sopt.org.motivooServer.global.advice.ErrorType;
import sopt.org.motivooServer.global.advice.BusinessException;

public class UserException extends BusinessException {

	public UserException(ErrorType errorType) {
		super(errorType);
	}
}
