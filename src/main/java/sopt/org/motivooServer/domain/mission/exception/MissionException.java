package sopt.org.motivooServer.domain.mission.exception;

import sopt.org.motivooServer.global.advice.BusinessException;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

public class MissionException extends BusinessException {

	public MissionException(BusinessExceptionType exceptionType) {
		super(exceptionType);
	}
}
