package sopt.org.motivooServer.global.advice;

import org.springframework.http.HttpStatus;

public interface BusinessExceptionType {
	HttpStatus status();
	String message();
}
