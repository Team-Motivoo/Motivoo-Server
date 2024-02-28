package sopt.org.motivoo.api.controller.mission;

import static sopt.org.motivoo.common.response.SuccessType.*;
import static sopt.org.motivoo.domain.auth.config.jwt.JwtTokenProvider.*;
import static sopt.org.motivoo.external.s3.S3BucketDirectory.*;

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
import sopt.org.motivoo.api.controller.mission.dto.request.GoalStepRequest;
import sopt.org.motivoo.api.controller.mission.dto.request.MissionImgUrlRequest;
import sopt.org.motivoo.api.controller.mission.dto.request.MissionStatusRequest;
import sopt.org.motivoo.api.controller.mission.dto.request.TodayMissionChoiceRequest;
import sopt.org.motivoo.api.controller.mission.dto.response.GoalStepResponse;
import sopt.org.motivoo.api.controller.mission.dto.response.MissionHistoryResponse;
import sopt.org.motivoo.api.controller.mission.dto.response.OpponentGoalStepsResponse;
import sopt.org.motivoo.api.controller.mission.dto.response.TodayMissionResponse;
import sopt.org.motivoo.common.response.ApiResponse;
import sopt.org.motivoo.domain.mission.service.UserMissionService;
import sopt.org.motivoo.external.s3.PreSignedUrlResponse;
import sopt.org.motivoo.external.s3.S3BucketDirectory;
import sopt.org.motivoo.external.s3.S3Service;

@RestController
@RequestMapping("/mission")
@RequiredArgsConstructor
public class UserMissionController {

	private final UserMissionService userMissionService;
	private final S3Service s3Service;

	@GetMapping("/image")
	public ResponseEntity<ApiResponse<PreSignedUrlResponse>> getMissionImgUrl(@Valid @RequestBody final MissionImgUrlRequest request) {
		return ApiResponse.success(GET_MISSION_IMAGE_PRE_SIGNED_URL_SUCCESS,
			s3Service.getUploadPreSignedUrl(S3BucketDirectory.of(request.imgPrefix())));
	}

	@PatchMapping("/image")
	public ResponseEntity<ApiResponse<Void>> updateMissionStatus(@Valid @RequestBody final MissionStatusRequest request, final Principal principal) {
		String imgUrl = s3Service.getS3ImgUrl(MISSION_PREFIX, request.fileName());
		userMissionService.updateMissionSuccess(imgUrl, getUserFromPrincipal(principal));
		return ApiResponse.success(GET_MISSION_IMAGE_PRE_SIGNED_URL_SUCCESS);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<MissionHistoryResponse>> getUserMissionHistory(final Principal principal) {
		return ApiResponse.success(GET_MISSION_HISTORY_SUCCESS,
			MissionHistoryResponse.of(userMissionService.getUserMissionHistory(getUserFromPrincipal(principal))));
	}

	@PostMapping("/today/choice")
	public ResponseEntity<ApiResponse<TodayMissionResponse>> getTodayMission(final Principal principal) {
		//TODO location 지정 & 상태코드 반영
		return ApiResponse.success(GET_TODAY_MISSION_SUCCESS,
			TodayMissionResponse.of(userMissionService.getTodayMission(getUserFromPrincipal(principal))));
	}

	@PostMapping("/today")
	public ResponseEntity<ApiResponse<Object>> choiceTodayMission(@RequestBody final TodayMissionChoiceRequest request, final Principal principal) {
		Long userMissionId = userMissionService.choiceTodayMission(request.toServiceDto(), getUserFromPrincipal(principal));
		URI location = URI.create("/mission/today/" + userMissionId);

		return ResponseEntity.created(location).body(ApiResponse.successV2(CHOICE_TODAY_MISSION_SUCCESS));
	}

	@GetMapping("/opponent")
	public ResponseEntity<ApiResponse<OpponentGoalStepsResponse>> getOpponentGoalSteps(final Principal principal) {
		return ApiResponse.success(GET_TODAY_OPPONENT_GOAL_STEP_COUNT,
			OpponentGoalStepsResponse.of(userMissionService.getOpponentGoalSteps(getUserFromPrincipal(principal))));
	}

	@PatchMapping("/step")
	public ResponseEntity<ApiResponse<GoalStepResponse>> changeStepCount(@RequestBody final GoalStepRequest request, final Principal principal) {
		return ApiResponse.success(UPDATE_STEP_COUNT,
			GoalStepResponse.of(userMissionService.updateGoalStepCount(request.toServiceDto(), getUserFromPrincipal(principal))));
	}
}
