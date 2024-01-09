package sopt.org.motivooServer.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import sopt.org.motivooServer.domain.auth.dto.response.OauthTokenResponse;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.exception.UserExceptionType;
import sopt.org.motivooServer.domain.auth.repository.TokenRedisRepository;
import sopt.org.motivooServer.global.config.auth.JwtTokenProvider;
import sopt.org.motivooServer.domain.user.dto.request.UserProfile;
import sopt.org.motivooServer.domain.auth.dto.response.LoginResponse;
import sopt.org.motivooServer.domain.auth.dto.request.OauthTokenRequest;
import sopt.org.motivooServer.domain.user.entity.SocialPlatform;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.entity.UserType;
import sopt.org.motivooServer.domain.user.repository.UserRepository;

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
    public LoginResponse login(OauthTokenRequest tokenRequest) {
        String providerName = tokenRequest.getTokenType();
        ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);

        String refreshToken = jwtTokenProvider.createRefreshToken();
        User user = getUserProfile(providerName, tokenRequest, provider, refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(user.getId()));

        return buildLoginResponse(user, accessToken, refreshToken);
    }

    private LoginResponse buildLoginResponse(User user, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .id(user.getSocialId())
                .nickname(user.getNickname())
                .tokenType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public User getUserProfile(String providerName, OauthTokenRequest tokenRequest, ClientRegistration provider, String refreshToken) {
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenRequest);
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(providerName, userAttributes);
        SocialPlatform socialPlatform = getSocialPlatform(providerName);

        String providerId = oAuth2UserInfo.getProviderId();
        String nickName = oAuth2UserInfo.getNickName();
        User userEntity = userRepository.findBySocialId(providerId);

        if (userEntity != null) {
            updateRefreshToken(userEntity, refreshToken);
        } else {
            userEntity = saveUser(nickName, providerId, socialPlatform, tokenRequest, refreshToken);
        }

        return userEntity;
    }

    private OAuth2UserInfo getOAuth2UserInfo(String providerName, Map<String, Object> userAttributes) {
        if (providerName.equals("kakao")) {
            return new UserProfile(userAttributes);
        } else {
            throw new UserException(UserExceptionType.INVALID_SOCIAL_PLATFORM);
        }
    }

    private SocialPlatform getSocialPlatform(String providerName) {
        if (providerName.equals("kakao")) {
            return SocialPlatform.KAKAO;
        } else {
            throw new UserException(UserExceptionType.INVALID_SOCIAL_PLATFORM);
        }
    }

    public void updateRefreshToken(User userEntity, String refreshToken) {
        userEntity.updateRefreshToken(refreshToken);
    }

    public User saveUser(String nickName, String providerId, SocialPlatform socialPlatform, OauthTokenRequest tokenRequest, String refreshToken) {
        User newUser = User.builder()
                .nickname(nickName)
                .socialId(providerId)
                .socialPlatform(socialPlatform)
                .socialAccessToken(tokenRequest.getAccessToken())
                .socialRefreshToken(refreshToken)
                .type(UserType.NONE)
                .deleted(Boolean.FALSE)
                .build();
        userRepository.save(newUser);
        return newUser;
    }

    private Map<String, Object> getUserAttributes(ClientRegistration provider, OauthTokenRequest tokenRequest) {
        return WebClient.create()
                .get()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header -> header.setBearerAuth(tokenRequest.getAccessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    @Transactional
    public OauthTokenResponse reissue(Long userId, String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);

        String reissuedAccessToken = jwtTokenProvider.createAccessToken(String.valueOf(userId));
        String reissuedRefreshToken = jwtTokenProvider.createRefreshToken();
        OauthTokenResponse tokenResponse = new OauthTokenResponse(reissuedAccessToken, reissuedAccessToken);

        tokenRedisRepository.saveRefreshToken(reissuedRefreshToken, String.valueOf(userId));
        tokenRedisRepository.deleteRefreshToken(refreshToken);

        return tokenResponse;
    }

    @Transactional
    public void logout(String accessToken) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String refreshToken = userRepository.findRefreshTokenById(Long.parseLong(userId));

        tokenRedisRepository.saveBlockedToken(accessToken);
        tokenRedisRepository.deleteRefreshToken(refreshToken);
    }
}
