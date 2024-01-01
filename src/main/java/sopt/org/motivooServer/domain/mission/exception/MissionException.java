package sopt.org.motivooServer.domain.mission.exception;

import sopt.org.motivooServer.global.common.error.ErrorResponse;
import sopt.org.motivooServer.global.common.exception.BusinessException;

public class MissionException extends BusinessException {

	public MissionException(ErrorResponse.ErrorType errorType) {
		super(errorType);
	}
}
