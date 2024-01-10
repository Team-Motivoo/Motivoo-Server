package sopt.org.motivooServer.domain.parentchild.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.motivooServer.domain.auth.config.JwtTokenProvider;
import sopt.org.motivooServer.domain.health.dto.request.OnboardingRequest;
import sopt.org.motivooServer.domain.health.dto.response.OnboardingResponse;
import sopt.org.motivooServer.domain.parentchild.service.ParentChildService;
import sopt.org.motivooServer.global.response.ApiResponse;

import static sopt.org.motivooServer.global.response.SuccessType.ONBOARDING_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ParentChildController {
    private final ParentChildService parentChildService;
    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping("/user/exercise")
    public ResponseEntity<ApiResponse<OnboardingResponse>> onboardInput(@RequestHeader("Authorization") String accessToken,
                                                                        @Valid @RequestBody final OnboardingRequest request){

        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println(userId);
        return ApiResponse.success(ONBOARDING_SUCCESS, parentChildService.onboardInput(userId, request));
    }


}
