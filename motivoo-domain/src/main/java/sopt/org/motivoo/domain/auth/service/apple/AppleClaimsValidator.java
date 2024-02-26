package sopt.org.motivooServer.domain.auth.service.apple;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppleClaimsValidator {
    private final String iss;
    private final String clientId;

    public AppleClaimsValidator(
            @Value("${apple.iss}") String iss,
            @Value("${apple.client-id}") String clientId
    ) {
        this.iss = iss;
        this.clientId = clientId;
    }

    public boolean isValid(Claims claims) {
        return claims.getIssuer().contains(iss)
                && claims.getAudience().equals(clientId);
    }
}
