package sopt.org.motivooServer.domain.auth.service;

import static sopt.org.motivooServer.domain.auth.config.JwtTokenProvider.*;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import sopt.org.motivooServer.domain.auth.dto.response.OauthTokenResponse;
import sopt.org.motivooServer.domain.auth.service.apple.AppleLoginService;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.exception.UserExceptionType;
import sopt.org.motivooServer.domain.auth.repository.TokenRedisRepository;
import sopt.org.motivooServer.domain.auth.config.JwtTokenProvider;
import sopt.org.motivooServer.domain.user.dto.request.KakaoUserProfile;
import sopt.org.motivooServer.domain.auth.dto.response.LoginResponse;
import sopt.org.motivooServer.domain.auth.dto.request.OauthTokenRequest;
import sopt.org.motivooServer.domain.user.entity.SocialPlatform;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.entity.UserType;
import sopt.org.motivooServer.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OauthService {
    private static final String BEARER_TYPE = "Bearer";
    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final UserRepository userRepository;
    private final TokenRedisRepository tokenRedisRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final AppleLoginService appleLoginService;

    @Transactional
    public LoginResponse login(OauthTokenRequest tokenRequest) {
        String providerName = tokenRequest.tokenType();

        //카카오
        if (providerName.equals("kakao")) {
            ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);

            String refreshToken = jwtTokenProvider.createRefreshToken();
            User user = getUserProfile(providerName, tokenRequest, provider, refreshToken);
            String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(user.getId()));

            return getLoginResponse(user, accessToken, refreshToken);
        }

        //애플
        String socialId = appleLoginService.getAppleId(tokenRequest.accessToken());

        boolean isRegistered = userRepository.existsBySocialPlatformAndSocialId(SocialPlatform.of(tokenRequest.tokenType()), socialId);

        String refreshToken = jwtTokenProvider.createRefreshToken();
        if (!isRegistered) {
            User user = User.builder()
//                    .nickname(nickName)
                    .socialId(socialId)
                    .socialPlatform(SocialPlatform.of(tokenRequest.tokenType()))
                    .socialAccessToken(tokenRequest.accessToken())
                    .refreshToken(refreshToken)
                    .type(UserType.NONE)
                    .deleted(Boolean.FALSE)
                    .build();
            userRepository.save(user);
        }

        List<User> users = userRepository.findBySocialPlatformAndSocialId(SocialPlatform.of(tokenRequest.tokenType()), socialId);
        if (users.isEmpty()) throw new UserException(UserExceptionType.INVALID_USER_TYPE);
        User loginUser = users.get(0);

        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(loginUser.getId()));
        OauthTokenResponse tokenDto = new OauthTokenResponse(accessToken, refreshToken);
        loginUser.updateRefreshToken(tokenDto.refreshToken());

        log.info("JWT Access Token: ", loginUser.getNickname(), tokenDto.accessToken());
        return getLoginResponse(loginUser, accessToken, refreshToken);
    }

    private LoginResponse getLoginResponse(User user, String accessToken, String refreshToken) {
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
            return userEntity;
        }

        return saveUser(nickName, providerId, socialPlatform, tokenRequest, refreshToken);

    }

    private OAuth2UserInfo getOAuth2UserInfo(String providerName, Map<String, Object> userAttributes) {
        if (providerName.equals("kakao")) {
            return new KakaoUserProfile(userAttributes);
        }
        throw new UserException(UserExceptionType.INVALID_SOCIAL_PLATFORM);
    }

    private SocialPlatform getSocialPlatform(String providerName) {
        try {
            switch (providerName){
                case "kakao":
                    return SocialPlatform.KAKAO;
                case "apple":
                    return SocialPlatform.APPLE;
                default:
                    throw new UserException(UserExceptionType.INVALID_SOCIAL_PLATFORM);

            }
        }catch (FeignException e){
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
                .socialAccessToken(tokenRequest.accessToken())
                .refreshToken(refreshToken)
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
                .headers(header -> header.setBearerAuth(tokenRequest.accessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    @Transactional
    public void logout(String accessToken) {
        String refreshToken = userRepository.findRefreshTokenById(getAuthenticatedUser());

        tokenRedisRepository.saveBlockedToken(accessToken);
        tokenRedisRepository.deleteRefreshToken(refreshToken);
    }
}
