package sopt.org.motivooServer.domain.auth.service.apple;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.security.PublicKey;
import java.util.Map;
import sopt.org.motivooServer.domain.auth.controller.apple.AppleApiClient;
import sopt.org.motivooServer.domain.auth.dto.response.apple.ApplePublicKeys;
import sopt.org.motivooServer.global.advice.BusinessException;

import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.INVALID_APPLE_CLAIMS;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppleLoginService {

    private AppleApiClient appleApiClient;
    private final AppleJwtParser appleJwtParser;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public String getAppleId(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleApiClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);
        log.info("애플="+claims.getSubject());
        return claims.getSubject();
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new BusinessException(INVALID_APPLE_CLAIMS);
        }
    }

}
