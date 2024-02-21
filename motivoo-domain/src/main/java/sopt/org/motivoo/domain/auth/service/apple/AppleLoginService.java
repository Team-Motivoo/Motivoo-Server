package sopt.org.motivoo.domain.auth.service.apple;

import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

import java.security.PublicKey;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.common.advice.BusinessException;
import sopt.org.motivoo.domain.auth.dto.response.apple.ApplePublicKeys;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppleLoginService {

    // private AppleApiClient appleApiClient;
    private final AppleJwtParser appleJwtParser;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    /* public String getAppleId(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        // ApplePublicKeys applePublicKeys = appleApiClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);
        log.info("애플="+claims.getSubject());
        return claims.getSubject();
    }*/

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new BusinessException(INVALID_APPLE_CLAIMS);
        }
    }

}
