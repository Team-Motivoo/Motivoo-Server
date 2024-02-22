package sopt.org.motivoo.domain.mission.exception;

import sopt.org.motivoo.common.advice.BusinessException;
import sopt.org.motivoo.common.advice.BusinessExceptionType;

public class MissionException extends BusinessException {

	public MissionException(BusinessExceptionType exceptionType) {
		super(exceptionType);
	}
}
