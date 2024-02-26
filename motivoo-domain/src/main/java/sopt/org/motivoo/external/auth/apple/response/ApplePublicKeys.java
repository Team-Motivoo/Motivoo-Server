package sopt.org.motivoo.external.auth.apple.response;



import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.motivoo.common.advice.BusinessException;
import sopt.org.motivoo.domain.user.exception.UserExceptionType;

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
                .orElseThrow(() -> new BusinessException(UserExceptionType.INVALID_APPLE_PUBLIC_KEY));
    }

}