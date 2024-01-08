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
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Exception> handleException(final Exception e) {
		log.error("Error 발생:{}", e.getStackTrace());
		log.error("Error 발생메시지:{}", e.getMessage());
		return ResponseEntity.internalServerError().body(e);
	}


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
