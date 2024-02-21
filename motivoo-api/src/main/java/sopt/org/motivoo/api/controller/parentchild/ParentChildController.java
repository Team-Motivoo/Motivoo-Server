package sopt.org.motivoo.api.controller.parentchild;

import static sopt.org.motivoo.common.response.SuccessType.*;
import static sopt.org.motivoo.domain.auth.config.JwtTokenProvider.*;

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
import sopt.org.motivoo.api.controller.health.dto.request.OnboardingRequest;
import sopt.org.motivoo.api.controller.health.dto.response.CheckOnboardingResponse;
import sopt.org.motivoo.api.controller.health.dto.response.OnboardingResponse;
import sopt.org.motivoo.api.controller.parentchild.dto.request.InviteRequest;
import sopt.org.motivoo.api.controller.parentchild.dto.response.InviteResponse;
import sopt.org.motivoo.api.controller.parentchild.dto.response.MatchingResponse;
import sopt.org.motivoo.common.response.ApiResponse;
import sopt.org.motivoo.domain.parentchild.service.ParentchildService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParentChildController {
    private final ParentchildService parentchildService;
    @PostMapping("/user/exercise")
    public ResponseEntity<ApiResponse<OnboardingResponse>> onboardInput(@Valid @RequestBody final OnboardingRequest request,
                                                                        Principal principal){
        Long userId = getUserFromPrincipal(principal);
        log.info("유저 아이디="+userId);
        return ApiResponse.success(ONBOARDING_SUCCESS,
            OnboardingResponse.of(parentchildService.onboardInput(userId, request.toServiceDto())));
    }

    @GetMapping("/user/onboarding")
    public ResponseEntity<ApiResponse<CheckOnboardingResponse>> checkOnboardingInfo(Principal principal){
        Long userId = getUserFromPrincipal(principal);
        log.info("유저 아이디="+userId);
        return ApiResponse.success(CHECK_ONBOARDING_INFO_SUCCESS,
            CheckOnboardingResponse.of(parentchildService.checkOnboardingInfo(userId)));
    }

    @PatchMapping("/parentchild/match")
    public ResponseEntity<ApiResponse<InviteResponse>> validateInviteCode(@Valid @RequestBody final InviteRequest request,
        Principal principal){
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(INPUT_INVITE_CODE_SUCCESS,
            InviteResponse.of(parentchildService.validateInviteCode(userId, request.toServiceDto())));
    }

    @GetMapping("/onboarding/match")
    public ResponseEntity<ApiResponse<MatchingResponse>> validateMatching(Principal principal) {
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(MATCHING_SUCCESS,
            MatchingResponse.of(parentchildService.checkMatching(userId)));
    }

}
