package sopt.org.motivooServer.domain.auth.service.apple;

import org.springframework.stereotype.Component;
import sopt.org.motivooServer.domain.auth.dto.response.apple.ApplePublicKey;
import sopt.org.motivooServer.domain.auth.dto.response.apple.ApplePublicKeys;
import sopt.org.motivooServer.global.advice.BusinessException;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.CREATE_PUBLIC_KEY_EXCEPTION;

@Component
public class PublicKeyGenerator {

    private static final String SIGN_ALGORITHM_HEADER_KEY = "alg";
    private static final String KEY_ID_HEADER_KEY = "kid";
    private static final int POSITIVE_SIGN_NUMBER = 1;

    //Apple Public Key를 바탕으로 Public Key를 생성
    public PublicKey generatePublicKey(Map<String, String> headers, ApplePublicKeys applePublicKeys) {
        ApplePublicKey applePublicKey =
                applePublicKeys.getMatchesKey(headers.get(SIGN_ALGORITHM_HEADER_KEY), headers.get(KEY_ID_HEADER_KEY));

        return generatePublicKeyWithApplePublicKey(applePublicKey);
    }

    private PublicKey generatePublicKeyWithApplePublicKey(ApplePublicKey publicKey) {
        byte[] nBytes = Base64.getDecoder().decode(publicKey.getN());
        byte[] eBytes = Base64.getDecoder().decode(publicKey.getE());

        BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
        BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(publicKey.getKty());
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new BusinessException(CREATE_PUBLIC_KEY_EXCEPTION);
        }
    }
}