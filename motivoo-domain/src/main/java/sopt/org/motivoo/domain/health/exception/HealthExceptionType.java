package sopt.org.motivoo.domain.health.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.common.advice.BusinessExceptionType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum HealthExceptionType implements BusinessExceptionType {

	/**
	 * 400 Bad Request
	 */
	INVALID_HEALTH_NOTE(HttpStatus.BAD_REQUEST, "유효하지 않는 건강 주의사항입니다."),
	INVALID_EXERCISE_FREQUENCY(HttpStatus.BAD_REQUEST, "유효하지 않는 운동 횟수입니다."),
	INVALID_EXERCISE_TIME(HttpStatus.BAD_REQUEST, "유효하지 않는 운동 시간입니다."),
	INVALID_EXERCISE_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않는 운동 유형입니다."),

	INVALID_EXERCISE_LEVEL(HttpStatus.BAD_REQUEST, "유효하지 않는 유저 분류입니다."),

	NULL_VALUE_IS_EXERCISE(HttpStatus.BAD_REQUEST, "운동 여부는 Null 값이어서는 안 됩니다."),
	EXCEED_HEALTH_NOTES_RANGE(HttpStatus.BAD_REQUEST, "운동 특이 사항은 3개까지 선택 가능합니다."),

	/**
	 * 404 Not Found
	 */
	HEALTH_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 건강정보입니다."),
	NOT_EXIST_USER_HEALTH(HttpStatus.NOT_FOUND, "해당 유저의 건강정보가 존재하지 않습니다."),

	/**
	 * 409 CONFLICT
	 */
	EXIST_ONBOARDING_INFO(HttpStatus.CONFLICT, "이미 온보딩 정보를 입력하셨습니다.");
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
