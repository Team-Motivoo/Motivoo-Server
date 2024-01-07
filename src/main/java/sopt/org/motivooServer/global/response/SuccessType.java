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

	GET_MYPAGE_INFO_SUCCESS(HttpStatus.OK, "마이페이지 정보 조회에 성공했습니다."),
	GET_MYPAGE_MYINFO_SUCCESS(HttpStatus.OK, "마이페이지 나의 정보 조회에 성공했습니다."),
	GET_MYPAGE_EXERCISE_INFO_SUCCESS(HttpStatus.OK, "마이페이지 정보 조회에 성공했습니다."),


	;

	private final HttpStatus httpStatus;
	private final String message;

	public int getHttpStatusCode() {
		return httpStatus.value();
	}
}
