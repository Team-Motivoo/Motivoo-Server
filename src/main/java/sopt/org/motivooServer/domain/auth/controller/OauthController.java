package sopt.org.motivooServer.domain.auth.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.motivooServer.domain.auth.config.JwtTokenProvider;
import sopt.org.motivooServer.domain.auth.dto.request.RefreshRequest;
import sopt.org.motivooServer.domain.auth.dto.response.LoginResponse;
import sopt.org.motivooServer.domain.auth.dto.request.OauthTokenRequest;
import sopt.org.motivooServer.domain.auth.dto.response.OauthTokenResponse;
import sopt.org.motivooServer.domain.auth.service.OauthService;
import sopt.org.motivooServer.global.response.ApiResponse;
import static sopt.org.motivooServer.global.response.SuccessType.*;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthController {
    private final OauthService oauthService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/oauth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody OauthTokenRequest tokenRequest) {
        return ApiResponse.success(LOGIN_SUCCESS, oauthService.login(tokenRequest));
    }

    @PostMapping("/oauth/reissue")
    public ResponseEntity<ApiResponse<OauthTokenResponse>> reissue(
            @RequestHeader("Authorization") String refreshToken,
            @RequestBody final RefreshRequest request) {
        return ApiResponse.success(REISSUE_SUCCESS, tokenProvider.reissue(request.userId(), refreshToken));
    }

    @PostMapping("/oauth/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String accessToken) {
        oauthService.logout(accessToken);

        return ApiResponse.success(LOGOUT_SUCCESS);
    }

}
