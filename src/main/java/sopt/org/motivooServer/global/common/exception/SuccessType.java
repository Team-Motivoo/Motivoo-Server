package sopt.org.motivooServer.global.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessType {

	/**
	 * 200 Ok
	 */
	HEALTH_CHECK_SUCCESS(HttpStatus.OK, "헬스체크용 API 호출에 성공했습니다.")



	;

	private final HttpStatus httpStatus;
	private final String message;

	public int getHttpStatusCode() {
		return httpStatus.value();
	}
}
