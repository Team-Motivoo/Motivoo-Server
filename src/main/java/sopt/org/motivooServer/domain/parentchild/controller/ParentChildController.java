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
import sopt.org.motivooServer.domain.parentchild.service.ParentChildService;
import sopt.org.motivooServer.global.response.ApiResponse;

import java.security.Principal;

import static sopt.org.motivooServer.domain.auth.config.JwtTokenProvider.getUserFromPrincipal;
import static sopt.org.motivooServer.global.response.SuccessType.*;

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

    @PatchMapping("/parentchild/match")
    public ResponseEntity<ApiResponse<InviteResponse>> validateInviteCode(Principal principal,
                                                                          @Valid @RequestBody final InviteRequest request){
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(INPUT_INVITE_CODE_SUCCESS, parentChildService.validateInviteCode(userId, request));
    }

    @GetMapping("/onboarding/match")
    public ResponseEntity<ApiResponse<MatchingResponse>> validateMatching(Principal principal){
        Long userId = getUserFromPrincipal(principal);
        return ApiResponse.success(MATCHING_SUCCESS, parentChildService.checkMatching(userId));
    }

}
