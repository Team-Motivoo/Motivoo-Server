package sopt.org.motivoo.common.advice;

import org.springframework.http.HttpStatus;

public interface BusinessExceptionType {
	HttpStatus status();
	String message();

	default int getHttpStatusCode() {
		return status().value();
	}
}
