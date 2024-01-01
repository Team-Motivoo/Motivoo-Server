package sopt.org.motivooServer.global.common.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.global.common.error.ErrorResponse;
import sopt.org.motivooServer.global.common.exception.BusinessException;
import sopt.org.motivooServer.global.common.response.ApiResponse;

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
	public ResponseEntity<ErrorResponse> handleCustomException(final BusinessException e) {

		log.error("🚨🚨🚨 CustomException occured: {} 🚨🚨🚨", e.getMessage());

		return ResponseEntity.status(e.getHttpStatus())
			.body(ErrorResponse.of(e.getErrorType(), e.getMessage()));
	}
}
