package sopt.org.motivooServer.global.healthcheck;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import sopt.org.motivooServer.global.response.ApiResponse;

@RestController
@RequestMapping("/test")
public class SlackTestController {

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ApiResponse<Object>> exceptionTest() {
		throw new IllegalArgumentException();
	}
}
