package sopt.org.motivoo.api.controller.parentchild;

import static sopt.org.motivoo.common.response.SuccessType.*;
import static sopt.org.motivoo.domain.auth.config.jwt.JwtTokenProvider.*;

import java.net.URI;
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
import sopt.org.motivoo.api.controller.parentchild.dto.response.InviteReceiveResponse;
import sopt.org.motivoo.api.controller.parentchild.dto.response.InviteSendResponse;
import sopt.org.motivoo.api.controller.parentchild.dto.response.MatchingResponse;
import sopt.org.motivoo.common.response.ApiResponse;
import sopt.org.motivoo.domain.parentchild.dto.response.InviteSendResult;
import sopt.org.motivoo.domain.parentchild.service.ParentchildService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParentChildController {

    private final ParentchildService parentchildService;

    @PostMapping("/user/exercise")
    public ResponseEntity<ApiResponse<OnboardingResponse>> onboardInput(@Valid @RequestBody final OnboardingRequest request,
                                                                        Principal principal){

        return ApiResponse.success(ONBOARDING_SUCCESS,
            OnboardingResponse.of(parentchildService.onboardInput(getUserFromPrincipal(principal), request.toServiceDto())));
    }

    @GetMapping("/user/onboarding")
    public ResponseEntity<ApiResponse<CheckOnboardingResponse>> checkOnboardingInfo(Principal principal){

        return ApiResponse.success(CHECK_ONBOARDING_INFO_SUCCESS,
            CheckOnboardingResponse.of(parentchildService.checkOnboardingInfo(getUserFromPrincipal(principal))));
    }

    @PatchMapping("/parentchild/match")
    public ResponseEntity<ApiResponse<InviteReceiveResponse>> matchParentchildRelation(@Valid @RequestBody final InviteRequest request, Principal principal){

        return ApiResponse.success(INPUT_INVITE_CODE_SUCCESS,
            InviteReceiveResponse.of(parentchildService.matchRelation(getUserFromPrincipal(principal), request.toServiceDto())));
    }

    @GetMapping("/onboarding/match")
    public ResponseEntity<ApiResponse<MatchingResponse>> validateMatching(Principal principal) {

        return ApiResponse.success(MATCHING_SUCCESS,
            MatchingResponse.of(parentchildService.checkMatching(getUserFromPrincipal(principal))));
    }

    @PostMapping("/parentchild/invite")
    public ResponseEntity<ApiResponse<InviteSendResponse>> inviteParentchild(Principal principal) {
        InviteSendResult result = parentchildService.inviteMatchUser(getUserFromPrincipal(principal));
        URI location = URI.create("/parentchild/invite/" + result.parentchildId());
        return ResponseEntity.created(location).body(ApiResponse.successV2(SEND_INVITE_CODE_SUCCESS,
            InviteSendResponse.of(parentchildService.inviteMatchUser(getUserFromPrincipal(principal)))));
    }
}
