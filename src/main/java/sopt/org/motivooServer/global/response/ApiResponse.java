package sopt.org.motivooServer.global.response;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
}


