package sopt.org.motivooServer.global.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 400 Bad Request
	 */


	/**
	 * 500 Internal Server Error
	 */


	/**
	 * Custom Error
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {

		log.error("🚨🚨🚨 BusinessException occured: {} 🚨🚨🚨", e.getMessage());

		return ResponseEntity.status(e.getHttpStatus())
			.body(ErrorResponse.of(e.getExceptionType()));
	}
}
