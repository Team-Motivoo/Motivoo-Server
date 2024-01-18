package sopt.org.motivooServer.domain.parentchild.controller;

import static sopt.org.motivooServer.domain.auth.config.JwtTokenProvider.*;
import static sopt.org.motivooServer.global.response.SuccessType.*;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.health.dto.request.OnboardingRequest;
import sopt.org.motivooServer.domain.health.dto.response.CheckOnboardingResponse;
import sopt.org.motivooServer.domain.health.dto.response.OnboardingResponse;
import sopt.org.motivooServer.domain.parentchild.dto.request.InviteRequest;
import sopt.org.motivooServer.domain.parentchild.dto.response.InviteResponse;
import sopt.org.motivooServer.domain.parentchild.dto.response.MatchingResponse;
import sopt.org.motivooServer.domain.parentchild.service.ParentchildService;
import sopt.org.motivooServer.global.response.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParentChildController {
    private final ParentchildService parentchildService;
    @PostMapping("/user/exercise")
    public ResponseEntity<ApiResponse<OnboardingResponse>> onboardInput(@Valid @RequestBody final OnboardingRequest request,
                                                                        Principal principal){
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(ONBOARDING_SUCCESS, parentchildService.onboardInput(userId, request));
    }

    @GetMapping("/user/onboarding")
    public ResponseEntity<ApiResponse<CheckOnboardingResponse>> checkOnboardingInfo(Principal principal){
        Long userId = getUserFromPrincipal(principal);
        System.out.println("유저 아이디="+userId);
        return ApiResponse.success(CHECK_ONBOARDING_INFO_SUCCESS, parentchildService.checkOnboardingInfo(userId));
    }

    @PatchMapping("/parentchild/match")
    public ResponseEntity<ApiResponse<InviteResponse>> validateInviteCode(@Valid @RequestBody final InviteRequest request,
        Principal principal){
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(INPUT_INVITE_CODE_SUCCESS, parentchildService.validateInviteCode(userId, request));
    }

    @GetMapping("/onboarding/match")
    public ResponseEntity<ApiResponse<MatchingResponse>> validateMatching(Principal principal) {
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(MATCHING_SUCCESS, parentchildService.checkMatching(userId));
    }

}
