package sopt.org.motivoo.domain.auth.service;

import static sopt.org.motivoo.domain.auth.config.JwtTokenProvider.*;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.auth.config.JwtTokenProvider;
import sopt.org.motivoo.domain.auth.config.UserAuthentication;
import sopt.org.motivoo.domain.auth.dto.request.OauthTokenCommand;
import sopt.org.motivoo.domain.auth.dto.response.LoginResult;
import sopt.org.motivoo.domain.auth.repository.TokenRedisRepository;
import sopt.org.motivoo.domain.auth.service.apple.AppleLoginService;
import sopt.org.motivoo.domain.user.dto.request.KakaoUserProfile;
import sopt.org.motivoo.domain.user.entity.SocialPlatform;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.entity.UserType;
import sopt.org.motivoo.domain.user.exception.UserException;
import sopt.org.motivoo.domain.user.exception.UserExceptionType;
import sopt.org.motivoo.domain.user.repository.UserRetriever;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OauthService {

    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final UserRetriever userRetriever;
    private final TokenRedisRepository tokenRedisRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final AppleLoginService appleLoginService;

    @Transactional
    public LoginResult login(OauthTokenCommand tokenRequest) {
        String providerName = tokenRequest.tokenType();

        //카카오
        ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);

        String refreshToken = jwtTokenProvider.createRefreshToken();
        User user = getUserProfile(providerName, tokenRequest, provider, refreshToken);
        log.info("유저 아이디="+user.getId());

        String accessToken = jwtTokenProvider.createAccessToken(new UserAuthentication(user.getId(), null, null));
        tokenRedisRepository.saveRefreshToken(refreshToken, String.valueOf(user.getId()));
        return LoginResult.of(user, accessToken, refreshToken);

    }

    public User getUserProfile(String providerName, OauthTokenCommand tokenRequest, ClientRegistration provider, String refreshToken) {
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenRequest);
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(providerName, userAttributes);
        SocialPlatform socialPlatform = getSocialPlatform(providerName);

        String providerId = oAuth2UserInfo.getProviderId();
        String nickName = oAuth2UserInfo.getNickName();

        List<User> userEntity = userRetriever.getUsersBySocialId(providerId);

        //처음 로그인 하거나 탈퇴한 경우 -> 회원가입
        if(userEntity==null || isWithdrawn(userEntity)){
            return saveUser(nickName, providerId, socialPlatform, tokenRequest, refreshToken);
        }

        //로그인
        updateRefreshToken(userEntity.get(0), refreshToken);
        return userEntity.get(0);
    }

    boolean isWithdrawn(List<User> users){
        for(User user:users){
            if(!user.isDeleted()){
                return false;
            }
        }
        return true;
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

    public User saveUser(String nickName, String providerId, SocialPlatform socialPlatform, OauthTokenCommand tokenRequest, String refreshToken) {
        User newUser = User.builder()
                .nickname(nickName)
                .socialId(providerId)
                .socialPlatform(socialPlatform)
                .socialAccessToken(tokenRequest.accessToken())
                .refreshToken(refreshToken)
                .type(UserType.NONE)
                .deleted(Boolean.FALSE)
                .build();
        userRetriever.saveUser(newUser);
        return newUser;
    }

    private Map<String, Object> getUserAttributes(ClientRegistration provider, OauthTokenCommand tokenRequest) {
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
        String refreshToken = userRetriever.getRefreshTokenById(getAuthenticatedUser());

        tokenRedisRepository.saveBlockedToken(accessToken);
        tokenRedisRepository.deleteRefreshToken(refreshToken);
    }
}