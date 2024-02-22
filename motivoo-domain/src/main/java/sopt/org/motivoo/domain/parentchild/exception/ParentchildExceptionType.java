package sopt.org.motivoo.domain.parentchild.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.common.advice.BusinessExceptionType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ParentchildExceptionType implements BusinessExceptionType {

	/**
	 * 400 Bad Request
	 */
	NOT_EXIST_PARENTCHILD_USER(HttpStatus.BAD_REQUEST, "해당 유저와 매칭된 부모-자녀 유저가 없습니다."),
	INVALID_PARENTCHILD_RELATION(HttpStatus.BAD_REQUEST, "올바르지 않은 부모-자식 관계입니다."),

	/**
	 * 404 Not Found
	 */
	PARENTCHILD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 부모-자녀 관계입니다."),
	MATCHING_NOT_FOUND(HttpStatus.NOT_FOUND, "아직 매칭이 이루어지지 않았습니다."),
	INVITE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 초대코드입니다."),

	/**
	 * 409 Conflict
	 */
	MATCH_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 매칭이 완료되었습니다."),

	/**
	 * 500 Internal Server Error
	 */
	FAIL_TO_MATCH_PARENTCHILD(HttpStatus.INTERNAL_SERVER_ERROR, "초대코드로 매칭하기에 실패했습니다.")


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
