package sopt.org.motivooServer.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sopt.org.motivooServer.domain.auth.controller.apple.AppleClient;
import sopt.org.motivooServer.domain.auth.dto.response.apple.ApplePublicKey;
import sopt.org.motivooServer.domain.auth.dto.response.apple.ApplePublicKeys;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AppleClientTest {

    @Autowired
    private AppleClient appleClient;

    @Test
    @DisplayName("apple 서버와 통신하여 Apple public keys 응답을 받는다")
    void getPublicKeys() {
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        List<ApplePublicKey> keys = applePublicKeys.getKeys();

        boolean isRequestedKeysNonNull = keys.stream()
                .allMatch(this::isAllNotNull);
        assertThat(isRequestedKeysNonNull).isTrue();
    }

    private boolean isAllNotNull(ApplePublicKey applePublicKey) {
        return Objects.nonNull(applePublicKey.getKty()) && Objects.nonNull(applePublicKey.getKid()) &&
                Objects.nonNull(applePublicKey.getUse()) && Objects.nonNull(applePublicKey.getAlg()) &&
                Objects.nonNull(applePublicKey.getN()) && Objects.nonNull(applePublicKey.getE());
    }


}
