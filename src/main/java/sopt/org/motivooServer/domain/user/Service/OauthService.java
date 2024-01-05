package sopt.org.motivooServer.domain.user.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import sopt.org.motivooServer.global.config.oauth.JwtTokenProvider;
import sopt.org.motivooServer.domain.user.dto.request.OAuth2UserInfo;
import sopt.org.motivooServer.domain.user.dto.request.UserProfile;
import sopt.org.motivooServer.domain.user.dto.response.LoginResponse;
import sopt.org.motivooServer.domain.user.dto.response.OauthTokenResponse;
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
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(String providerName, OauthTokenResponse tokenResponse) {
        ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);

        User user = getUserProfile(providerName, tokenResponse, provider);

        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = jwtTokenProvider.createRefreshToken();

        return LoginResponse.builder()
                .id(user.getSocialId())
                .nickname(user.getNickname())
                .tokenType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    private User getUserProfile(String providerName, OauthTokenResponse tokenResponse, ClientRegistration provider){
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenResponse);
        OAuth2UserInfo oAuth2UserInfo = null;
        SocialPlatform socialPlatform = null;
        if(providerName.equals("kakao")){
            oAuth2UserInfo = new UserProfile(userAttributes);
            socialPlatform = SocialPlatform.KAKAO;
        } else {
            log.info("허용되지 않은 접근 입니다.");
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String nickName = oAuth2UserInfo.getNickName();

        User userEntity = userRepository.findBySocialId(providerId);

        if(userEntity == null){
            userEntity = User.builder()
                    .nickname(nickName)
                    .socialId(providerId)
                    .socialPlatform(socialPlatform)
                    .socialAccessToken(tokenResponse.getAccessToken())
                    .type(UserType.NONE)
                    .deleted(Boolean.FALSE)
                    .build();
            userRepository.save(userEntity);
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



}
