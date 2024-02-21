package sopt.org.motivoo.common.response;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sopt.org.motivoo.common.advice.BusinessExceptionType;
import sopt.org.motivoo.common.advice.ErrorType;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"code", "message", "success", "data"})
public class ApiResponse<T> {

	private final int code;
	private final String message;
	private final boolean success;

	@JsonInclude(JsonInclude.Include.NON_NULL) // null이 아니면 포함 => 응답으로 전달할 값들
	private T data;

	// 성공
	public static <T> ResponseEntity<ApiResponse<T>> success(SuccessType successType) {
		return ResponseEntity.status(successType.getHttpStatus())
			.body(ApiResponse.<T>builder()
				.code(successType.getHttpStatusCode())
				.message(successType.getMessage())
				.success(true).build());
	}

	public static <T> ResponseEntity<ApiResponse<T>> success(SuccessType successType, T data) {
		return ResponseEntity.status(successType.getHttpStatus())
			.body(ApiResponse.<T>builder()
				.code(successType.getHttpStatusCode())
				.message(successType.getMessage())
				.success(true)
				.data(data).build());
	}

	public static <T> ApiResponse<T> successV2(SuccessType successType) {
		return ApiResponse.<T>builder()
				.code(successType.getHttpStatusCode())
				.message(successType.getMessage())
				.success(true).build();
	}

	// 실패
	public static <T> ResponseEntity<ApiResponse<T>> fail(BusinessExceptionType exceptionType, T data) {
		return ResponseEntity.status(exceptionType.status())
			.body(ApiResponse.<T>builder()
				.code(exceptionType.getHttpStatusCode())
				.message(exceptionType.message())
				.success(false)
				.data(data).build());
	}

	public static <T> ResponseEntity<ApiResponse<T>> fail(ErrorType errorType, T data) {
		return ResponseEntity.status(errorType.getHttpStatus())
			.body(ApiResponse.<T>builder()
				.code(errorType.getHttpStatusCode())
				.message(errorType.message())
				.success(false)
				.data(data).build());
	}
}


