package sopt.org.motivooServer.domain.user.exception;

import sopt.org.motivooServer.global.common.exception.ErrorType;
import sopt.org.motivooServer.global.common.exception.BusinessException;

public class UserException extends BusinessException {

	public UserException(ErrorType errorType) {
		super(errorType);
	}
}
