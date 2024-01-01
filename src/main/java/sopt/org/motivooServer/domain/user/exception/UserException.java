package sopt.org.motivooServer.domain.user.exception;

import sopt.org.motivooServer.global.common.error.ErrorResponse;
import sopt.org.motivooServer.global.common.exception.BusinessException;

public class UserException extends BusinessException {

	public UserException(ErrorResponse.ErrorType errorType) {
		super(errorType);
	}
}
