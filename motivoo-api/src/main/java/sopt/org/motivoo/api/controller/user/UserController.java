package sopt.org.motivoo.api.controller.user;

import static sopt.org.motivoo.common.response.SuccessType.*;
import static sopt.org.motivoo.domain.auth.config.jwt.JwtTokenProvider.*;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.api.controller.user.dto.response.MyHealthInfoResponse;
import sopt.org.motivoo.api.controller.user.dto.response.MyPageInfoResponse;
import sopt.org.motivoo.common.response.ApiResponse;
import sopt.org.motivoo.domain.user.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MyPageInfoResponse>> getMyInfo(Principal principal) {
		return ApiResponse.success(GET_MYINFO_SUCCESS,
			MyPageInfoResponse.of(userService.getMyInfo(getUserFromPrincipal(principal))));
	}

	@GetMapping("/exercise")
	public ResponseEntity<ApiResponse<MyHealthInfoResponse>> getMyExercise(Principal principal) {
		return ApiResponse.success(GET_MYPAGE_HEALTH_INFO_SUCCESS,
			MyHealthInfoResponse.of(userService.getMyHealthInfo(getUserFromPrincipal(principal))));
	}

}
