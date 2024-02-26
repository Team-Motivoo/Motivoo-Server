package sopt.org.motivooServer.global.response;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessType {

	/**
	 * 200 Ok
	 */
	HEALTH_CHECK_SUCCESS(HttpStatus.OK, "헬스체크용 API 호출에 성공했습니다."),
	IMAGE_S3_UPLOAD_SUCCESS(HttpStatus.OK, "S3 버킷에 이미지를 업로드하는 데 성공했습니다."),
	IMAGE_S3_DELETE_SUCCESS(HttpStatus.OK, "S3 버킷에 이미지를 삭제하는 데 성공했습니다."),
	UPDATE_MISSION_TEXT(HttpStatus.OK, "미션 내용 개행 추가에 성공했습니다."),
	UPDATE_MISSION_ICON(HttpStatus.OK, "미션 내용 기본 아이콘 추가에 성공했습니다."),

	//미션
	GET_MISSION_IMAGE_PRE_SIGNED_URL_SUCCESS(HttpStatus.OK, "미션 인증사진의 Presigned Url을 생성하는 데 성공했습니다."),
	GET_MISSION_HISTORY_SUCCESS(HttpStatus.OK, "이전 운동 미션 히스토리를 조회하는 데 성공했습니다."),
	GET_TODAY_OPPONENT_GOAL_STEP_COUNT(HttpStatus.OK, "오늘의 상대 목표 걸음 수를 조회하는 데 성공했습니다."),
	UPDATE_STEP_COUNT(HttpStatus.OK, "목표 걸음 수 변경에 성공했습니다."),

	//유저
	GET_MYINFO_SUCCESS(HttpStatus.OK, "마이페이지 나의 정보 조회에 성공했습니다."),
	GET_MYPAGE_HEALTH_INFO_SUCCESS(HttpStatus.OK, "마이페이지 건강 정보 조회에 성공했습니다."),

	MISSION_STEP_COUNT_STATUS_SUCCESS(HttpStatus.OK, "홈 화면 미션 걸음 수 달성 정보 조회에 성공했습니다."),

	//소셜 로그인
	LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
	LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다."),
	REISSUE_SUCCESS(HttpStatus.OK, "토큰 재발급에 성공했습니다."),
	SIGNOUT_SUCCESS(HttpStatus.OK, "회원탈퇴에 성공했습니다."),


	//온보딩
	ONBOARDING_SUCCESS(HttpStatus.CREATED, "온보딩 질문 입력에 성공했습니다."),
	INPUT_INVITE_CODE_SUCCESS(HttpStatus.OK, "초대코드 입력에 성공했습니다."),
	MATCHING_SUCCESS(HttpStatus.OK, "매칭에 성공했습니다."),
	CHECK_ONBOARDING_INFO_SUCCESS(HttpStatus.OK, "운동정보 완료 여부 확인에 성공했습니다."),

	/**
	 * 201 Created
	 */

	CHOICE_TODAY_MISSION_SUCCESS(HttpStatus.CREATED, "오늘의 미션 선정에 성공했습니다."),

	GET_TODAY_MISSION_SUCCESS(HttpStatus.CREATED, "오늘의 미션을 조회하는 데 성공했습니다."),

	/**
	 * 204 No Content
	 */


	;

	private final HttpStatus httpStatus;
	private final String message;

	public int getHttpStatusCode() {
		return httpStatus.value();
	}
}
