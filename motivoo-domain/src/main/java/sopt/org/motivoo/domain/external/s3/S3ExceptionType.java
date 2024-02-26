package sopt.org.motivooServer.global.external.s3;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum S3ExceptionType implements BusinessExceptionType {

	/**
	 * 400 Bad Request
	 */
	INVALID_BUCKET_PREFIX(HttpStatus.BAD_REQUEST, "유효하지 않는 S3 버킷 디렉터리 이름입니다."),

	/**
	 * 404 Not Found
	 */

	/**
	 * 500 Internal Server Error
	 */
	FAIL_TO_UPLOAD_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "S3 버킷에 이미지를 업로드하는 데 실패했습니다."),
	FAIL_TO_GET_IMAGE_PRE_SIGNED_URL(HttpStatus.INTERNAL_SERVER_ERROR, "S3 버킷에 업로드할 경로를 조회하는 데 실패했습니다."),
	FAIL_TO_DELETE_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "S3 버킷으로부터 이미지를 삭제하는 데 실패했습니다.")

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
