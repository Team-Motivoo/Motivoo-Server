package sopt.org.motivooServer.domain.mission.exception;

import sopt.org.motivooServer.global.common.exception.ErrorType;
import sopt.org.motivooServer.global.common.exception.BusinessException;

public class MissionException extends BusinessException {

	public MissionException(ErrorType errorType) {
		super(errorType);
	}
}
