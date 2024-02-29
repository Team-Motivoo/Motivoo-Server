package sopt.org.motivoo.domain.auth.repository;


import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Repository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.auth.config.RedisConfig;
import sopt.org.motivoo.domain.user.exception.UserException;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository {

    private final RedisConfig redisConfig;

    @Value("${jwt.refresh-token.expire-length}")
    private long refreshTokenValidityInMilliseconds;
    private static final String BEARER_TYPE = "Bearer";

    private static final String PREFIX_REFRESH = "REFRESH:";
    private static final String PREFIX_BLOCKED = "BLOCKED:";

    @Value("${jwt.token.secret-key}")
    private String secretKey;


    public void saveRefreshToken(String refreshToken, String account) {
        RedisTemplate<String, String> redisTemplate = redisConfig.redisTemplate();
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String key = PREFIX_REFRESH + refreshToken;
        valueOperations.set(key, account);
        redisTemplate.expire(key, refreshTokenValidityInMilliseconds, TimeUnit.SECONDS);
    }

    public void saveBlockedToken(String accessToken) {
        RedisTemplate<String, String> redisTemplate = redisConfig.redisTemplate();
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String key = PREFIX_BLOCKED + accessToken;
        valueOperations.set(key, "empty");

        Date expiration = getExpirationFromToken(accessToken);
        setExpirationInRedis(key, expiration);
    }

    public Date getExpirationFromToken(String accessToken) {
        try {
            accessToken = accessToken.replaceAll("\\s+", "");
            accessToken = accessToken.replace(BEARER_TYPE, "");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            return claims.getExpiration();
        } catch (JwtException e) {
            throw new UserException(TOKEN_EXPIRED);
        }
    }

    private void setExpirationInRedis(String key, Date expiration) {
        RedisTemplate<String, String> redisTemplate = redisConfig.redisTemplate();

        Long now = new Date().getTime();
        Long remainTime = expiration.getTime() - now;
        redisTemplate.expire(key, remainTime, TimeUnit.SECONDS);
    }

    public Optional<String> findByRefreshToken(String refreshToken) {
        RedisTemplate<String, String> redisTemplate = redisConfig.redisTemplate();
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String key = PREFIX_REFRESH + refreshToken;
        return Optional.ofNullable(valueOperations.get(key));
    }

    public boolean doesTokenBlocked(String accessToken) {
        RedisTemplate<String, String> redisTemplate = redisConfig.redisTemplate();
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String key = PREFIX_BLOCKED + accessToken;
        return valueOperations.get(key) != null;
    }

    public void deleteRefreshToken(String refreshToken) {
        RedisTemplate<String, String> redisTemplate = redisConfig.redisTemplate();

        String key = PREFIX_REFRESH + refreshToken;
        redisTemplate.delete(key);
    }
}