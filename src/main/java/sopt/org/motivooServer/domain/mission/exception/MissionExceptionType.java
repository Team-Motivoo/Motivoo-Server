package sopt.org.motivooServer.domain.mission.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MissionExceptionType implements BusinessExceptionType {

	/**
	 * 404 Not Found
	 */
	MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 미션입니다."),
	USER_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저 미션입니다.")


	;

	private final HttpStatus status;
	private final String message;

	@Override
	public HttpStatus status() {
		return this.status;
	}

	@Override
	public String message() {
		return this.message;
	}
}
