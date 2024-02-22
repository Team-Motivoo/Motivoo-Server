package sopt.org.motivoo.api.controller.mission;


import static sopt.org.motivoo.common.response.SuccessType.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.common.response.ApiResponse;
import sopt.org.motivoo.domain.mission.service.MissionService;

@RestController
@RequiredArgsConstructor
public class MissionController {

	private final MissionService missionService;

	@PatchMapping("/util/mission")
	public ResponseEntity<ApiResponse<Object>> updateMissionContentTxt() {
		missionService.updateMissionContentText();
		return ApiResponse.success(UPDATE_MISSION_TEXT);
	}

	@PatchMapping("/util/icon")
	public ResponseEntity<ApiResponse<Object>> updateMissionDefaultIcon() {
		missionService.updateMissionDefaultIcon();
		return ApiResponse.success(UPDATE_MISSION_ICON);
	}
}
