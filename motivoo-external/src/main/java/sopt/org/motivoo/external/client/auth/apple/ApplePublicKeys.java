package sopt.org.motivoo.external.client.auth.apple;

import static sopt.org.motivoo.common.advice.CommonExceptionType.*;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.motivoo.common.advice.BusinessException;

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