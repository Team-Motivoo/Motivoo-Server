package sopt.org.motivooServer.domain.mission.controller;

import static sopt.org.motivooServer.global.response.SuccessType.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.mission.dto.request.MissionImgUrlRequest;
import sopt.org.motivooServer.domain.mission.dto.response.MissionImgUrlResponse;
import sopt.org.motivooServer.domain.mission.service.UserMissionService;
import sopt.org.motivooServer.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mission")
public class UserMissionController {

	private final UserMissionService userMissionService;

	// TODO 항상 유저의 오늘의 미션에만 인증 사진 등록이 가능하므로, @PathVariable은 생략할 예정
	@PatchMapping("/image/{missionId}")
	public ResponseEntity<ApiResponse<MissionImgUrlResponse>> getMissionImgUrl(@RequestBody final MissionImgUrlRequest request,
																			@PathVariable final Long missionId) {
		return ApiResponse.success(GET_MISSION_IMAGE_PRE_SIGNED_URL_SUCCESS,
			userMissionService.getMissionImgUrl(request, missionId));
	}
}
