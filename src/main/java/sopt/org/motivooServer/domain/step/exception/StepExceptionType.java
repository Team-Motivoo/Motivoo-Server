package sopt.org.motivooServer.domain.step.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.global.advice.BusinessExceptionType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum StepExceptionType implements BusinessExceptionType {

	/**
	 * 400 Bad Request
	 */


	/**
	 * 404 Not Found
	 */


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
