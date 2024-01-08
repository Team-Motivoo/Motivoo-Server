package sopt.org.motivooServer.domain.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.motivooServer.domain.user.dto.response.LoginResponse;
import sopt.org.motivooServer.domain.user.dto.response.OauthTokenResponse;
import sopt.org.motivooServer.domain.user.service.OauthService;
import sopt.org.motivooServer.global.response.ApiResponse;
import static sopt.org.motivooServer.global.response.SuccessType.LOGIN_SUCCESS;
import static sopt.org.motivooServer.global.response.SuccessType.LOGOUT_SUCCESS;


@RequiredArgsConstructor
@Slf4j
@RestController
public class OauthController {
    private final OauthService oauthService;

    @GetMapping("/oauth/login/{provider}")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@PathVariable String provider, @RequestBody OauthTokenResponse tokenResponse){
        LoginResponse loginResponse = oauthService.login(provider, tokenResponse);

        return ApiResponse.success(LOGIN_SUCCESS, loginResponse);
    }

    @PostMapping("/oauth/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String accessToken){
        oauthService.logout(accessToken);

        return ApiResponse.success(LOGOUT_SUCCESS);
    }

}
