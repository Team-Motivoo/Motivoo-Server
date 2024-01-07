package sopt.org.motivooServer.global.advice;

import static sopt.org.motivooServer.global.advice.ErrorType.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.global.util.slack.SlackUtil;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

	private final SlackUtil slackUtil;

	/**
	 * 400 Bad Request
	 */


	/**
	 * 500 Internal Server Error
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(final Exception e, final HttpServletRequest request) throws IOException {
		slackUtil.sendAlert(e, request);

		log.error("🔔🚨 Slack에 전송된 Error Log: {}", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ErrorResponse.of(INTERNAL_SERVER_ERROR));
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
