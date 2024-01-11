package sopt.org.motivooServer.domain.parentchild.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ParentchildExceptionType implements BusinessExceptionType {

	/**
	 * 404 Not Found
	 */
	PARENTCHILD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 부모-자녀 관계입니다."),

	/**
	 * 409 Conflict
	 */
	MATCH_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 매칭이 완료되었습니다.");
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
