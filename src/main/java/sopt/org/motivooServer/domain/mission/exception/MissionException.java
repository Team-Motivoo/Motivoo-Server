package sopt.org.motivooServer.domain.mission.exception;

import sopt.org.motivooServer.global.advice.ErrorType;
import sopt.org.motivooServer.global.advice.BusinessException;

public class MissionException extends BusinessException {

	public MissionException(ErrorType errorType) {
		super(errorType);
	}
}
