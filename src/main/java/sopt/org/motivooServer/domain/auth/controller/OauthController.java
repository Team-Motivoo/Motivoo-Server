package sopt.org.motivooServer.domain.auth.controller;

import static sopt.org.motivooServer.global.response.SuccessType.*;

import java.security.Principal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.auth.config.JwtTokenProvider;
import sopt.org.motivooServer.domain.auth.dto.request.OauthTokenRequest;
import sopt.org.motivooServer.domain.auth.dto.request.RefreshRequest;
import sopt.org.motivooServer.domain.auth.dto.response.LoginResponse;
import sopt.org.motivooServer.domain.auth.dto.response.OauthTokenResponse;
import sopt.org.motivooServer.domain.auth.service.OauthService;
import sopt.org.motivooServer.domain.user.service.UserService;
import sopt.org.motivooServer.global.response.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OauthController {
    private final OauthService oauthService;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping("/oauth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody OauthTokenRequest tokenRequest) {
        return ApiResponse.success(LOGIN_SUCCESS, oauthService.login(tokenRequest));
    }

    @PostMapping("/oauth/reissue")
    public ResponseEntity<ApiResponse<OauthTokenResponse>> reissue(
            @RequestHeader("Authorization") String refreshToken,
            @RequestBody RefreshRequest request) {
        return ApiResponse.success(REISSUE_SUCCESS, tokenProvider.reissue(request.userId(), refreshToken));
    }

    @PostMapping("/oauth/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String accessToken) {
        oauthService.logout(accessToken);

        return ApiResponse.success(LOGOUT_SUCCESS);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Object>> signout(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        log.info("유저 아이디="+userId);
        userService.deleteSocialAccount(userId);

        return ApiResponse.success(SIGNOUT_SUCCESS);
    }
}
