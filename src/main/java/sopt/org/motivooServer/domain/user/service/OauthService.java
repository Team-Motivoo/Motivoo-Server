package sopt.org.motivooServer.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.exception.UserExceptionType;
import sopt.org.motivooServer.domain.user.repository.TokenRedisRepository;
import sopt.org.motivooServer.global.config.oauth.JwtTokenProvider;
import sopt.org.motivooServer.domain.user.dto.oauth.OAuth2UserInfo;
import sopt.org.motivooServer.domain.user.dto.request.UserProfile;
import sopt.org.motivooServer.domain.user.dto.response.LoginResponse;
import sopt.org.motivooServer.domain.user.dto.response.OauthTokenResponse;
import sopt.org.motivooServer.domain.user.entity.SocialPlatform;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.entity.UserType;
import sopt.org.motivooServer.domain.user.repository.UserRepository;
import sopt.org.motivooServer.global.config.oauth.JwtValidationType;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OauthService {
    private static final String BEARER_TYPE = "Bearer";
    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final UserRepository userRepository;
    private final TokenRedisRepository tokenRedisRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Transactional
    public LoginResponse login(String providerName, OauthTokenResponse tokenResponse) {
        ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);

        String refreshToken = jwtTokenProvider.createRefreshToken();
        User user = getUserProfile(providerName, tokenResponse, provider, refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(user.getId()));

        reissue(user.getId(), refreshToken); //refresh token 재발급

        return LoginResponse.builder()
                .id(user.getSocialId())
                .nickname(user.getNickname())
                .tokenType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    @Transactional
    public User getUserProfile(String providerName, OauthTokenResponse tokenResponse, ClientRegistration provider, String refreshToken){
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenResponse);
        OAuth2UserInfo oAuth2UserInfo = null;
        SocialPlatform socialPlatform = null;
        if(providerName.equals("kakao")){
            oAuth2UserInfo = new UserProfile(userAttributes);
            socialPlatform = SocialPlatform.KAKAO;
        } else {
            throw new UserException(UserExceptionType.INVALID_SOCIAL_PLATFORM);
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String nickName = oAuth2UserInfo.getNickName();

        User userEntity = userRepository.findBySocialId(providerId)
                .orElseThrow(
                        () -> new UserException(UserExceptionType.USER_NOT_FOUND));

        if(userEntity == null){
            userEntity = User.builder()
                    .nickname(nickName)
                    .socialId(providerId)
                    .socialPlatform(socialPlatform)
                    .socialAccessToken(tokenResponse.getAccessToken())
                    .socialRefreshToken(refreshToken)
                    .type(UserType.NONE)
                    .deleted(Boolean.FALSE)
                    .build();
            userRepository.save(userEntity);
        }
        else{
            userEntity.updateRefreshToken(refreshToken);
        }
        return userEntity;
    }

    private Map<String, Object> getUserAttributes(ClientRegistration provider, OauthTokenResponse tokenResponse){
        return WebClient.create()
                .get()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header -> header.setBearerAuth(tokenResponse.getAccessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>(){})
                .block();
    }

    @Transactional
    public String reissue(Long userId, String refreshToken){
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserException(UserExceptionType.USER_NOT_FOUND));

        if(jwtTokenProvider.validateToken(refreshToken)!= JwtValidationType.VALID_JWT){
            throw new UserException(UserExceptionType.TOKEN_EXPIRED);
        }

        String reissuedToken = jwtTokenProvider.createRefreshToken();

        tokenRedisRepository.saveRefreshToken(reissuedToken, String.valueOf(userId));
        tokenRedisRepository.deleteRefreshToken(refreshToken);

        return reissuedToken;
    }

    @Transactional
    public void logout(String accessToken){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String refreshToken = userRepository.findRefreshTokenById(Long.parseLong(userId));

        tokenRedisRepository.saveBlockedToken(accessToken);
        tokenRedisRepository.deleteRefreshToken(refreshToken);
    }

}
