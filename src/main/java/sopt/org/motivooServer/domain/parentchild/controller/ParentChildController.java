package sopt.org.motivooServer.domain.parentchild.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.motivooServer.domain.health.dto.request.OnboardingRequest;
import sopt.org.motivooServer.domain.health.dto.response.OnboardingResponse;
import sopt.org.motivooServer.domain.parentchild.dto.request.InviteRequest;
import sopt.org.motivooServer.domain.parentchild.dto.response.InviteResponse;
import sopt.org.motivooServer.domain.parentchild.dto.response.MatchingResponse;
import sopt.org.motivooServer.domain.parentchild.service.ParentchildService;
import sopt.org.motivooServer.global.response.ApiResponse;

import java.security.Principal;

import static sopt.org.motivooServer.domain.auth.config.JwtTokenProvider.getUserFromPrincipal;
import static sopt.org.motivooServer.global.response.SuccessType.*;

@Slf4j
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ParentChildController {
    private final ParentchildService parentchildService;
    @PostMapping("/user/exercise")
    public ResponseEntity<ApiResponse<OnboardingResponse>> onboardInput(@Valid @RequestBody final OnboardingRequest request,
                                                                        Principal principal){
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(ONBOARDING_SUCCESS, parentchildService.onboardInput(userId, request));
    }

    @PatchMapping("/parentchild/match")
    public ResponseEntity<ApiResponse<InviteResponse>> validateInviteCode(@Valid @RequestBody final InviteRequest request,
                                                                          Principal principal){
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(INPUT_INVITE_CODE_SUCCESS, parentchildService.validateInviteCode(userId, request));
    }

    @GetMapping("/onboarding/match")
    public ResponseEntity<ApiResponse<MatchingResponse>> validateMatching(Principal principal){
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(MATCHING_SUCCESS, parentchildService.checkMatching(userId));
    }

}
