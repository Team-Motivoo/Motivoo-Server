package sopt.org.motivooServer.domain.mission.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MissionExceptionType implements BusinessExceptionType {

	/**
	 * 400 Bad Request
	 */
	EMPTY_USER_MISSIONS(HttpStatus.BAD_REQUEST, "유저 미션 리스트가 비어 있습니다."),
	INVALID_MISSION_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 미션 타입 Enum 값입니다."),
	NOT_FILTERED_TODAY_MISSION(HttpStatus.BAD_REQUEST, "아직 오늘의 미션 선택지가 정해지지 않았습니다."),
	NOT_EXIST_TODAY_MISSION_CHOICE(HttpStatus.BAD_REQUEST, "미션 선택지에 존재하지 않는 미션 ID 값입니다."),
	ALREADY_CHOICE_TODAY_MISSION(HttpStatus.BAD_REQUEST, "이미 오늘의 미션을 선정했습니다."),
	NOT_COMPLETE_MISSION_STEPS_SUCCESS(HttpStatus.BAD_REQUEST, "아직 미션 걸음 수를 달성하지 못해 인증사진을 등록할 수 없습니다."),


	/**
	 * 404 Not Found
	 */
	MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 미션입니다."),
	USER_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저 미션입니다."),
	NOT_CHOICE_TODAY_MISSION(HttpStatus.NOT_FOUND, "아직 오늘의 미션을 선정하지 않았습니다."),
	MISSION_QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 미션 퀘스트입니다."),

	/**
	 * 500 Internal Server Error
	 */
	FAIL_TO_GET_TODAY_MISSION(HttpStatus.INTERNAL_SERVER_ERROR, "오늘의 미션 조회에 실패했습니다."),
	FAIL_TO_UPDATE_GOAL_STEP_COUNT(HttpStatus.INTERNAL_SERVER_ERROR, "목표 걸음 수 변경에 실패했습니다.")



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
