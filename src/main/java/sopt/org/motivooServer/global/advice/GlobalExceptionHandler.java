package sopt.org.motivooServer.global.advice;

import static sopt.org.motivooServer.global.advice.CommonExceptionType.*;
import static sopt.org.motivooServer.global.advice.ErrorType.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.global.response.ApiResponse;
import sopt.org.motivooServer.global.external.slack.SlackService;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

	private final SlackService slackService;

	/**
	 * 400 Bad Request
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {

		Errors errors = e.getBindingResult();
		Map<String, String> validateDetails = new HashMap<>();

		for (FieldError error : errors.getFieldErrors()) {
			String validKeyName = String.format("valid_%s", error.getField());
			validateDetails.put(validKeyName, error.getDefaultMessage());
		}
		return ApiResponse.fail(REQUEST_VALIDATION_EXCEPTION, validateDetails);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
		log.error("GlobalExceptionHandler에서 잡은 Exception: {}", e.getStackTrace());
		ErrorResponse ex = ErrorResponse.of(VALIDATION_WRONG_HTTP_REQUEST);
		return ResponseEntity.status(ex.code()).body(ex);
	}

	// Header에 원하는 Key가 없는 경우
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingRequestHeaderException.class)
	protected ResponseEntity<ErrorResponse> handlerMissingRequestHeaderException(final MissingRequestHeaderException e) {
		log.error("GlobalExceptionHandler에서 잡은 Exception: {}", e.getStackTrace());
		ErrorResponse ex = ErrorResponse.of(HEADER_REQUEST_MISSING_EXCEPTION);
		return ResponseEntity.status(ex.code()).body(ex);
	}

	/**
	 * 405 Method Not Allowed
	 */
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> handlerHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
		log.error("GlobalExceptionHandler에서 잡은 Exception: {}", e.getStackTrace());
		ErrorResponse ex = ErrorResponse.of(INVALID_HTTP_METHOD);
		return ResponseEntity.status(ex.code()).body(ex);
	}

	/**
	 * 500 Internal Server Error
	 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e, final HttpServletRequest request) throws IOException {
        slackService.sendAlert(e, request);

        log.error("🔔🚨 Slack에 전송된 Error Log: {}", e.getMessage());
		ErrorResponse ex = ErrorResponse.of(INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(ex.code()).body(ex);
    }

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(IndexOutOfBoundsException.class)
	public ResponseEntity<ErrorResponse> handleIndexOutOfBoundsException(final IndexOutOfBoundsException e) {
		log.error("GlobalExceptionHandler에서 잡은 Exception: {}", e.getStackTrace());
		ErrorResponse ex = ErrorResponse.of(INDEX_OUT_OF_BOUND_ERROR);
		return ResponseEntity.status(ex.code()).body(ex);
	}

	/*@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ErrorResponse> handleNullPointerException(final NullPointerException e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ErrorResponse.of(NULL_POINTER_ACCESS_ERROR));
	}*/


	/**
	 * Custom Error
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {

		log.error("🚨🚨🚨 BusinessException occured: {} 🚨🚨🚨", e.getStackTrace());

		return ResponseEntity.status(e.getHttpStatus())
				.body(ErrorResponse.of(e.getExceptionType()));
	}
}
