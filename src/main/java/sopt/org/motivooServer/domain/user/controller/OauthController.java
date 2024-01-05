package sopt.org.motivooServer.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.motivooServer.domain.user.Service.OauthService;
import sopt.org.motivooServer.domain.user.dto.response.LoginResponse;
import sopt.org.motivooServer.domain.user.dto.response.OauthTokenResponse;

@RequiredArgsConstructor
@Slf4j
@RestController
public class OauthController {
    private final OauthService oauthService;

    @GetMapping("/login/oauth/{provider}")
    public ResponseEntity<LoginResponse> login(@PathVariable String provider, @RequestBody OauthTokenResponse tokenResponse){
        LoginResponse loginResponse = oauthService.login(provider, tokenResponse);

        return ResponseEntity.ok().body(loginResponse);
    }


}
