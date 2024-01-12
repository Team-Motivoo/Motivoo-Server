package sopt.org.motivooServer.domain.mission.controller;

import static sopt.org.motivooServer.domain.auth.config.JwtTokenProvider.*;
import static sopt.org.motivooServer.global.response.SuccessType.*;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.mission.dto.request.MissionStepStatusRequest;
import sopt.org.motivooServer.domain.mission.dto.response.MissionStepStatusResponse;
import sopt.org.motivooServer.domain.mission.service.UserMissionService;
import sopt.org.motivooServer.global.response.ApiResponse;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

	private final UserMissionService userMissionService;

	@PatchMapping
	public ResponseEntity<ApiResponse<MissionStepStatusResponse>> getMissionCompleted(@RequestBody final MissionStepStatusRequest request, final Principal principal) {
		return ApiResponse.success(MISSION_STEP_COUNT_STATUS_SUCCESS,
			userMissionService.getMissionCompleted(request, getUserFromPrincipal(principal)));
	}
}