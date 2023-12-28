package sopt.org.motivooServer.domain;

import static sopt.org.motivooServer.global.common.exception.SuccessType.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sopt.org.motivooServer.global.common.response.ApiResponse;

@RestController
@RequestMapping("/health")
public class HealthCheck {

	@GetMapping
	public ApiResponse<String> healthCheck() {
		return ApiResponse.success(HEALTH_CHECK_SUCCESS, "test success!");
	}
}
