package sopt.org.motivoo.domain.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import sopt.org.motivoo.domain.user.exception.UserException;

import java.util.Date;

import static sopt.org.motivoo.domain.user.exception.UserExceptionType.TOKEN_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class TokenRedisRetriever {
    private final TokenRedisRepository tokenRedisRepository;
    public void saveRefreshToken(String refreshToken, String account) {
       tokenRedisRepository.saveRefreshToken(refreshToken, account);
    }


    public void saveBlockedToken(String accessToken) {
       tokenRedisRepository.saveBlockedToken(accessToken);
    }

    private Date getExpirationFromToken(String accessToken) {
        return tokenRedisRepository.getExpirationFromToken(accessToken);
    }


    public String getRefreshToken(String refreshToken) {
        return tokenRedisRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new UserException(TOKEN_NOT_FOUND));
    }


    public void deleteRefreshToken(String refreshToken) {
        tokenRedisRepository.deleteRefreshToken(refreshToken);
    }
}
