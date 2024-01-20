package sopt.org.motivooServer.domain.mission.controller;

import static sopt.org.motivooServer.domain.auth.config.JwtTokenProvider.*;
import static sopt.org.motivooServer.global.response.SuccessType.*;

import java.net.URI;
import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.mission.dto.request.MissionImgUrlRequest;
import sopt.org.motivooServer.domain.mission.dto.request.TodayMissionChoiceRequest;
import sopt.org.motivooServer.domain.mission.dto.response.MissionHistoryResponse;
import sopt.org.motivooServer.domain.mission.dto.response.MissionImgUrlResponse;
import sopt.org.motivooServer.domain.mission.dto.response.OpponentGoalStepsResponse;
import sopt.org.motivooServer.domain.mission.dto.response.TodayMissionResponse;
import sopt.org.motivooServer.domain.mission.service.UserMissionService;
import sopt.org.motivooServer.global.response.ApiResponse;

@RestController
@RequestMapping("/mission")
@RequiredArgsConstructor
public class UserMissionController {

	private final UserMissionService userMissionService;

	@PatchMapping("/image")
	public ResponseEntity<ApiResponse<MissionImgUrlResponse>> getMissionImgUrl(@Valid @RequestBody final MissionImgUrlRequest request, final Principal principal) {
		return ApiResponse.success(GET_MISSION_IMAGE_PRE_SIGNED_URL_SUCCESS,
			userMissionService.getMissionImgUrl(request, getAuthenticatedUser()));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<MissionHistoryResponse>> getUserMissionHistory(final Principal principal) {
		return ApiResponse.success(GET_MISSION_HISTORY_SUCCESS, userMissionService.getUserMissionHistory(getUserFromPrincipal(principal)));
	}

	@PostMapping("/today/choice")
	public ResponseEntity<ApiResponse<TodayMissionResponse>> getTodayMission(final Principal principal) {
		//TODO location 지정 & 상태코드 반영
		return ApiResponse.success(GET_TODAY_MISSION_SUCCESS, userMissionService.getTodayMission(getUserFromPrincipal(principal)));
	}

	@PostMapping("/today")
	public ResponseEntity<ApiResponse<Object>> choiceTodayMission(@RequestBody final TodayMissionChoiceRequest request, final Principal principal) {
		Long userMissionId = userMissionService.choiceTodayMission(request, getUserFromPrincipal(principal));
		URI location = URI.create("/today" + userMissionId);

		return ResponseEntity.created(location).body(ApiResponse.successV2(CHOICE_TODAY_MISSION_SUCCESS));
	}

	@GetMapping("/opponent")
	public ResponseEntity<ApiResponse<OpponentGoalStepsResponse>> getOpponentGoalSteps(final Principal principal) {
		return ApiResponse.success(GET_TODAY_OPPONENT_GOAL_STEP_COUNT, userMissionService.getOpponentGoalSteps(getUserFromPrincipal(principal)));
	}
}
