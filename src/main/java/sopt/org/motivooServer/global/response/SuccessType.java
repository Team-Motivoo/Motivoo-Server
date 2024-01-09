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

	GET_MISSION_IMAGE_PRE_SIGNED_URL_SUCCESS(HttpStatus.OK, "미션 인증사진의 Presigned Url을 생성하는 데 성공했습니다."),

	//소셜 로그인
	LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
	LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다."),
	REISSUE_SUCCESS(HttpStatus.OK, "토큰 재발급에 성공했습니다.");
	;

	private final HttpStatus httpStatus;
	private final String message;

	public int getHttpStatusCode() {
		return httpStatus.value();
	}
}
