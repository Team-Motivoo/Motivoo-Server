package sopt.org.motivooServer.domain.user.controller;

import static sopt.org.motivooServer.domain.auth.config.JwtTokenProvider.*;
import static sopt.org.motivooServer.global.response.SuccessType.*;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.user.dto.response.MyHealthInfoResponse;
import sopt.org.motivooServer.domain.user.dto.response.MyPageInfoResponse;
import sopt.org.motivooServer.domain.user.service.UserService;
import sopt.org.motivooServer.global.response.ApiResponse;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MyPageInfoResponse>> getMyInfo(Principal principal) {
		return ApiResponse.success(GET_MYINFO_SUCCESS, userService.getMyInfo(getUserFromPrincipal(principal)));
	}

	@GetMapping("/exercise")
	public ResponseEntity<ApiResponse<MyHealthInfoResponse>> getMyExercise(Principal principal) {
		return ApiResponse.success(GET_MYPAGE_HEALTH_INFO_SUCCESS, userService.getMyHealthInfo(getUserFromPrincipal(principal)));
	}

}
