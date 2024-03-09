package sopt.org.motivoo.api.controller.mission;

import static sopt.org.motivoo.common.response.SuccessType.*;
import static sopt.org.motivoo.domain.auth.config.jwt.JwtTokenProvider.*;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.api.controller.mission.dto.response.StepStatusResponse;
import sopt.org.motivoo.common.response.ApiResponse;
import sopt.org.motivoo.domain.mission.service.UserMissionService;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

	private final UserMissionService userMissionService;

	@GetMapping
	public ResponseEntity<ApiResponse<StepStatusResponse>> getMissionCompleted(final Principal principal) {
		return ApiResponse.success(MISSION_STEP_COUNT_STATUS_SUCCESS,
			StepStatusResponse.of(userMissionService.getMissionCompleted(getUserFromPrincipal(principal))));
	}
}