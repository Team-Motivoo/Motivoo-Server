package sopt.org.motivooServer.global.common.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.global.common.exception.CustomException;
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
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse> handleCustomException(final CustomException e) {

		log.error("🚨🚨🚨 CustomException occured: {} 🚨🚨🚨", e.getMessage());

		return ResponseEntity.status(e.getHttpStatus())
			.body(ApiResponse.error(e.getErrorType(), e.getMessage()));
	}
}
