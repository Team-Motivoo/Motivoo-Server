package sopt.org.motivooServer.domain.user.controller;

import static sopt.org.motivooServer.global.response.SuccessType.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.user.dto.response.MyHealthInfoResponse;
import sopt.org.motivooServer.domain.user.dto.response.MyPageInfoResponse;
import sopt.org.motivooServer.domain.user.service.UserService;
import sopt.org.motivooServer.global.response.ApiResponse;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping   // TODO @PathVariable -> Principal
	public ResponseEntity<ApiResponse<MyPageInfoResponse>> getMyPage(@PathVariable final Long userId) {
		return ApiResponse.success(GET_MYPAGE_INFO_SUCCESS, userService.getMyPage(userId));
	}

	@GetMapping("/info")   // TODO @PathVariable -> Principal
	public ResponseEntity<ApiResponse<MyPageInfoResponse>> getMyInfo(@PathVariable final Long userId) {
		return ApiResponse.success(GET_MYPAGE_MYINFO_SUCCESS, userService.getMyInfo(userId));
	}

	@GetMapping("/exercise")  // TODO @PathVariable -> Principal
	public ResponseEntity<ApiResponse<MyHealthInfoResponse>> getMyExercise(@PathVariable final Long userId) {
		return ApiResponse.success(GET_MYPAGE_EXERCISE_INFO_SUCCESS, userService.getMyHealthInfo(userId));
	}
}
