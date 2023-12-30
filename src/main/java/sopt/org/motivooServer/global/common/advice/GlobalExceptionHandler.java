package sopt.org.motivooServer.global.common.advice;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.global.common.exception.CustomException;
import sopt.org.motivooServer.global.common.exception.ErrorType;
import sopt.org.motivooServer.global.common.response.ApiResponse;
import sopt.org.motivooServer.global.util.slack.SlackUtil;

@Slf4j
@RestControllerAdvice
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final SlackUtil slackUtil;

	/**
	 * 400 Bad Request
	 */


	/**
	 * 500 Internal Server Error
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	protected ApiResponse<Object> handleException(final Exception e, final HttpServletRequest request) throws IOException {
		slackUtil.sendAlert(e, request);
		return ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR);
	}


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
