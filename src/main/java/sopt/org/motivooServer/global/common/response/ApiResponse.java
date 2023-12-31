package sopt.org.motivooServer.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.global.common.error.ErrorType;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"status", "message", "data"})
public class ApiResponse<T> {

	private final int code;
	private final String message;
	private final boolean success;

	@JsonInclude(JsonInclude.Include.NON_NULL) // null이 아니면 포함 => 응답으로 전달할 값들
	private T data;

	// 성공
	public static ApiResponse success(SuccessType successType) {
		return ApiResponse.builder()
			.code(successType.getHttpStatusCode())
			.message(successType.getMessage())
			.success(true).build();
	}

	public static <T> ApiResponse<T> success(SuccessType successType, T data) {
		return ApiResponse.<T>builder()
			.code(successType.getHttpStatusCode())
			.message(successType.getMessage())
			.success(true)
			.data(data).build();
	}

	// 실패
	public static ApiResponse error(ErrorType errorType) {
		return ApiResponse.builder()
			.code(errorType.getHttpStatusCode())
			.message(errorType.getMessage())
			.success(false).build();
	}

	public static ApiResponse error(ErrorType errorType, String message) {
		return ApiResponse.builder()
			.code(errorType.getHttpStatusCode())
			.message(message)
			.success(false).build();
	}

	public static ApiResponse<Object> error(ErrorType errorType, Exception e) {
		return ApiResponse.builder()
			.code(errorType.getHttpStatusCode())
			.message(errorType.getMessage())
			.success(false)
			.data(e).build();
	}
}


