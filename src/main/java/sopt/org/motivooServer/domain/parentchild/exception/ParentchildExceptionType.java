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
	NOT_EXIST_PARENTCHILD_USER(HttpStatus.NOT_FOUND, "해당 유저와 매칭된 부모-자녀 유저가 없습니다.")


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
