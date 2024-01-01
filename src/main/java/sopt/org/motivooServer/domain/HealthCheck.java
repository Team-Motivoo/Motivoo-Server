package sopt.org.motivooServer.domain;

import static sopt.org.motivooServer.global.common.response.SuccessType.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sopt.org.motivooServer.global.common.response.ApiResponse;

@RestController
@RequestMapping("/health")
public class HealthCheck {

	@GetMapping
	public ResponseEntity<ApiResponse<String>> healthCheck() {
		return ApiResponse.success(HEALTH_CHECK_SUCCESS, "test success!");
	}
}
