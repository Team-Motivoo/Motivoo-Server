package sopt.org.motivoo.external.client.auth.apple.service;


import static sopt.org.motivoo.common.advice.CommonExceptionType.*;

import java.security.PublicKey;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.common.advice.BusinessException;
import sopt.org.motivoo.external.client.auth.apple.AppleClient;
import sopt.org.motivoo.external.client.auth.apple.ApplePublicKeys;
import sopt.org.motivoo.external.client.auth.apple.service.dto.OAuthPlatformMemberResult;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppleLoginService {

    private AppleClient appleClient;
    private final AppleJwtParser appleJwtParser;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public OAuthPlatformMemberResult getApplePlatformMember(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);
        return new OAuthPlatformMemberResult(claims.getSubject(), claims.get("email", String.class));
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new BusinessException(INVALID_APPLE_CLAIMS);
        }
    }

}