package sopt.org.motivoo.api.controller.mission;

import static sopt.org.motivoo.common.response.SuccessType.*;
import static sopt.org.motivoo.domain.auth.config.JwtTokenProvider.*;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.api.controller.mission.dto.request.MissionStepStatusRequest;
import sopt.org.motivoo.api.controller.mission.dto.response.MissionStepStatusResponse;
import sopt.org.motivoo.common.response.ApiResponse;
import sopt.org.motivoo.domain.mission.service.UserMissionService;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

	private final UserMissionService userMissionService;

	@PatchMapping
	public ResponseEntity<ApiResponse<MissionStepStatusResponse>> getMissionCompleted(@Valid @RequestBody final MissionStepStatusRequest request, final Principal principal) {
		return ApiResponse.success(MISSION_STEP_COUNT_STATUS_SUCCESS,
			MissionStepStatusResponse.of(userMissionService.getMissionCompleted(request.toServiceDto(), getUserFromPrincipal(principal))));
	}
}