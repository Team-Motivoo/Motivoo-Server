package sopt.org.motivooServer.domain.auth.dto.response.apple;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.motivooServer.global.advice.BusinessException;

import java.util.List;

import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.INVALID_APPLE_PUBLIC_KEY;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ApplePublicKeys {

    private List<ApplePublicKey> keys;

    public ApplePublicKey getMatchesKey(String alg, String kid) {
        return this.keys
                .stream()
                .filter(k -> k.getAlg().equals(alg) && k.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new BusinessException(INVALID_APPLE_PUBLIC_KEY));
    }

}