package sopt.org.motivooServer.domain.user.repository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Repository;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository {
    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-token.expire-length}")
    private long refreshTokenValidityInMilliseconds;
    private final static String PREFIX_REFRESH = "REFRESH:";
    private final static String PREFIX_BLOCKED = "BLOCKED:";

    @Value("${jwt.token.secret-key}")
    private String secretKey;
    
    public void saveRefreshToken(String refreshToken, String account) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = PREFIX_REFRESH + refreshToken;
        valueOperations.set(key, account);
        redisTemplate.expire(key, refreshTokenValidityInMilliseconds, TimeUnit.SECONDS);
    }


    public void saveBlockedToken(String accessToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = PREFIX_BLOCKED + accessToken;
        valueOperations.set(key, "empty");

        Date expiration = null;

        try {
            accessToken = accessToken.replaceAll("\\s+", "");
            accessToken = accessToken.replace("Bearer", "");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            expiration = claims
                    .getExpiration();

        } catch (JwtException e){
            throw new RuntimeException("유효하지 않은 토큰 입니다");
        }


        Long now = new Date().getTime();
        Long remainTime = expiration.getTime() - now;
        redisTemplate.expire(key, remainTime, TimeUnit.SECONDS);
    }

    public Optional<String> findByRefreshToken(String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = PREFIX_REFRESH + refreshToken;
        return Optional.ofNullable(valueOperations.get(key));
    }


    public boolean doesTokenBlocked(String accessToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = PREFIX_BLOCKED + accessToken;
        return valueOperations.get(key) != null;
    }


    public void deleteRefreshToken(String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = PREFIX_REFRESH + refreshToken;
        redisTemplate.delete(key);
    }
}