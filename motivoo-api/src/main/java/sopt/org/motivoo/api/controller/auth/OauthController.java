package sopt.org.motivoo.api.controller.auth;

import static sopt.org.motivoo.common.response.SuccessType.*;
import static sopt.org.motivoo.domain.auth.config.jwt.JwtTokenProvider.*;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.api.controller.auth.dto.request.OauthTokenRequest;
import sopt.org.motivoo.api.controller.auth.dto.request.RefreshRequest;
import sopt.org.motivoo.api.controller.auth.dto.response.LoginResponse;
import sopt.org.motivoo.api.controller.auth.dto.response.OauthTokenResponse;
import sopt.org.motivoo.common.response.ApiResponse;
import sopt.org.motivoo.domain.auth.config.jwt.JwtTokenProvider;
import sopt.org.motivoo.domain.auth.service.OauthService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OauthController {
    private final OauthService oauthService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/oauth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody OauthTokenRequest tokenRequest) {
        return ApiResponse.success(LOGIN_SUCCESS,
            LoginResponse.of(oauthService.login(tokenRequest.toServiceDto())));
    }

    @PostMapping("/oauth/reissue")
    public ResponseEntity<ApiResponse<OauthTokenResponse>> reissue(
            @RequestHeader("Authorization") String refreshToken,
            @RequestBody RefreshRequest request) {
        return ApiResponse.success(REISSUE_SUCCESS,
            OauthTokenResponse.of(tokenProvider.reissue(request.userId(), refreshToken)));
    }

    @PostMapping("/oauth/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String accessToken) {
        oauthService.logout(accessToken);

        return ApiResponse.success(LOGOUT_SUCCESS);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Object>> signout(Principal principal) {
        oauthService.signout(getUserFromPrincipal(principal));

        return ApiResponse.success(SIGNOUT_SUCCESS);
    }
}
