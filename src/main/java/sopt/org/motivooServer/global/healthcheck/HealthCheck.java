package sopt.org.motivooServer.global.healthcheck;

import static sopt.org.motivooServer.global.response.SuccessType.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import sopt.org.motivooServer.global.response.ApiResponse;

@RestController
@RequestMapping("/health")
public class HealthCheck implements HealthCheckApi {

	@GetMapping
	public ResponseEntity<ApiResponse<String>> healthCheck() {
		return ApiResponse.success(HEALTH_CHECK_SUCCESS, "test success!");
	}

	// @ResponseStatus VS ResponseEntity
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<String> healthCheckV2() {
		return ApiResponse.successV2(HEALTH_CHECK_SUCCESS);
	}
}
