package sopt.org.motivooServer.global.healthcheck;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.sentry.Sentry;
import sopt.org.motivooServer.global.response.ApiResponse;

@RestController
@RequestMapping("/test")
public class SlackTestController {

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ApiResponse<Object>> exceptionTest() {
		throw new IllegalArgumentException();
	}


	//== Sentry 로깅 테스트 ==//
	@GetMapping("/sentry")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> exceptionTestV2() {
		try {
			throw new Exception("This is a test.");
		} catch (Exception e) {
			Sentry.captureException(e);
		}
		return ResponseEntity.ok("Success Send to Sentry!");
	}

	@GetMapping(name = "Sentry 트랜잭션 테스트", value = "transcation")
	public void sentryTransactionTest() {
		System.out.println("sentryTransactionTest");
	}

	@GetMapping(name = "Sentry Exception Test", value = "exception")
	public void sentryExceptionTest() throws Exception {
		System.out.println("sentryExceptionTest");
		throw new Exception("test");
	}
}
