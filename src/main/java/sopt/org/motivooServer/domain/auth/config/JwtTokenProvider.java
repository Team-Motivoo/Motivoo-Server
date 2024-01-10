package sopt.org.motivooServer.domain.auth.config;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import sopt.org.motivooServer.domain.auth.dto.response.OauthTokenResponse;
import sopt.org.motivooServer.domain.auth.repository.TokenRedisRepository;
import sopt.org.motivooServer.domain.user.exception.UserException;

import java.util.*;

import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final String BEARER_TYPE = "Bearer";

    @Value("${jwt.access-token.expire-length}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token.expire-length}")
    private long refreshTokenValidityInMilliseconds;

    @Value("${jwt.token.secret-key}")
    private String secretKey;

    private TokenRedisRepository tokenRedisRepository;

    public JwtTokenProvider(TokenRedisRepository tokenRedisRepository){
        this.tokenRedisRepository = tokenRedisRepository;
    }

    public String createAccessToken(String payload) {
        return createToken(payload, accessTokenValidityInMilliseconds);
    }

    public String createRefreshToken() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        String generatedString = Base64.getEncoder().encodeToString(array);
        return createToken(generatedString, refreshTokenValidityInMilliseconds);
    }

    public String createToken(String payload, long expireLength) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("payload", payload);
        Date now = new Date();
        Date validity = new Date(now.getTime() + expireLength);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }

    public String getPayload(String token){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Object subject = claims.get("payload");

            return String.valueOf(subject);

        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (JwtException e){
            throw new RuntimeException(String.valueOf(JwtValidationType.INVALID_JWT_TOKEN));
        }
    }

    public void validateToken(String token) {
        try {
            System.out.println(token);
            token = token.replaceAll("\\s+", "");
            token = token.replace(BEARER_TYPE, "");
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (MalformedJwtException ex){
            throw new UserException(TOKEN_NOT_FOUND);
        } catch (ExpiredJwtException ex) {
            throw new UserException(TOKEN_EXPIRED);
        } catch (UnsupportedJwtException ex) {
            throw new UserException(TOKEN_UNSUPPORTED);
        } catch (IllegalArgumentException ex) {
            throw new UserException(TOKEN_NOT_FOUND);
        }
    }


    public OauthTokenResponse reissue(Long userId, String refreshToken) {
        validateToken(refreshToken);

        String reissuedAccessToken = createAccessToken(String.valueOf(userId));
        String reissuedRefreshToken = createRefreshToken();
        OauthTokenResponse tokenResponse = new OauthTokenResponse(reissuedAccessToken, reissuedAccessToken);

        tokenRedisRepository.saveRefreshToken(reissuedRefreshToken, String.valueOf(userId));
        tokenRedisRepository.deleteRefreshToken(refreshToken);

        return tokenResponse;
    }
}