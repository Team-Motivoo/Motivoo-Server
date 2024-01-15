package sopt.org.motivooServer.domain.mission.controller;

import static sopt.org.motivooServer.global.response.SuccessType.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.mission.service.MissionService;
import sopt.org.motivooServer.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
public class MissionController {

	private final MissionService missionService;

	@PatchMapping("/util/mission")
	public ResponseEntity<ApiResponse<Object>> updateMissionContentTxt() {
		missionService.updateMissionContentText();
		return ApiResponse.success(UPDATE_MISSION_TEXT);
	}
}
