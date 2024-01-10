package sopt.org.motivooServer.domain.parentchild.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.motivooServer.domain.health.dto.request.OnboardingRequest;
import sopt.org.motivooServer.domain.health.dto.response.OnboardingResponse;
import sopt.org.motivooServer.domain.parentchild.service.ParentChildService;
import sopt.org.motivooServer.global.response.ApiResponse;

import java.security.Principal;

import static sopt.org.motivooServer.domain.auth.config.JwtTokenProvider.getUserFromPrincipal;
import static sopt.org.motivooServer.global.response.SuccessType.ONBOARDING_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ParentChildController {
    private final ParentChildService parentChildService;
    @PostMapping("/user/exercise")
    public ResponseEntity<ApiResponse<OnboardingResponse>> onboardInput(Principal principal,
                                                                        @Valid @RequestBody final OnboardingRequest request){
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(ONBOARDING_SUCCESS, parentChildService.onboardInput(userId, request));
    }
}
