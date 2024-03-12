package sopt.org.motivoo.domain.auth.service;

import static sopt.org.motivoo.domain.auth.config.jwt.JwtTokenProvider.*;
import static sopt.org.motivoo.domain.user.entity.SocialPlatform.*;
import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.auth.config.UserAuthentication;
import sopt.org.motivoo.domain.auth.config.jwt.JwtTokenProvider;
import sopt.org.motivoo.domain.auth.dto.request.OauthTokenCommand;
import sopt.org.motivoo.domain.auth.dto.response.LoginResult;
import sopt.org.motivoo.domain.auth.repository.TokenRedisRetriever;
import sopt.org.motivoo.domain.health.repository.HealthRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionChoicesRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionRetriever;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.parentchild.repository.ParentchildRetriever;
import sopt.org.motivoo.domain.user.dto.request.KakaoUserProfile;
import sopt.org.motivoo.domain.user.entity.SocialPlatform;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.entity.UserType;
import sopt.org.motivoo.domain.user.exception.UserException;
import sopt.org.motivoo.domain.user.repository.UserRetriever;
import sopt.org.motivoo.domain.user.service.UserManager;
import sopt.org.motivoo.external.client.auth.apple.service.AppleLoginService;
import sopt.org.motivoo.external.client.auth.apple.service.dto.OAuthPlatformMemberResult;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OauthService {
    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final UserRetriever userRetriever;
    private final TokenRedisRetriever tokenRedisRetriever;

    private final UserManager userManager;
    private final HealthRetriever healthRetriever;
    private final UserMissionRetriever userMissionRetriever;
    private final UserMissionChoicesRetriever userMissionChoicesRetriever;
    private final ParentchildRetriever parentchildRetriever;

    private final JwtTokenProvider jwtTokenProvider;
    private final AppleLoginService appleLoginService;

    @Transactional
    public LoginResult login(OauthTokenCommand tokenRequest) {
        String providerName = tokenRequest.tokenType();
        log.info("소셜플랫폼="+providerName);
        SocialPlatform socialPlatform = SocialPlatform.of(providerName);
        String refreshToken = jwtTokenProvider.createRefreshToken();


        if (socialPlatform.equals(KAKAO)) {
            ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);

            User user = getUserProfile(providerName, tokenRequest, provider, refreshToken);
            log.info("유저 아이디="+user.getId());

            String accessToken = jwtTokenProvider.createAccessToken(new UserAuthentication(user.getId(), null, null));
            tokenRedisRetriever.saveRefreshToken(refreshToken, String.valueOf(user.getId()));

            boolean isFinishedOnboarding = healthRetriever.existsHealthByUser(user);
            boolean isMatched = user.getParentchild() != null && user.getParentchild().isMatched();

            return LoginResult.of(user, accessToken, refreshToken, isFinishedOnboarding, isMatched);
        }

        if (socialPlatform.equals(APPLE)) {
            OAuthPlatformMemberResult applePlatformMember = appleLoginService.getApplePlatformMember(tokenRequest.accessToken());

            List<User> userEntity = userRetriever.getUsersBySocialId(applePlatformMember.platformId());
            //처음 로그인 하거나 탈퇴한 경우 -> 회원가입
            if (userEntity == null || isWithdrawn(userEntity)) {
                saveUser(null, applePlatformMember.platformId(), socialPlatform, tokenRequest, refreshToken);
            }

            //로그인
            User user = userEntity.get(0);
            updateRefreshToken(user, refreshToken);
            String accessToken = jwtTokenProvider.createAccessToken(new UserAuthentication(user.getId(),null,null));

            boolean isFinishedOnboarding = healthRetriever.existsHealthByUser(user);
            boolean isMatched = user.getParentchild() != null && user.getParentchild().isMatched();

            return LoginResult.of(userEntity.get(0), accessToken, refreshToken, isFinishedOnboarding, isMatched);
        }
        throw new UserException(INVALID_SOCIAL_PLATFORM);
    }


    public User getUserProfile(String providerName, OauthTokenCommand tokenRequest, ClientRegistration provider, String refreshToken) {
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenRequest);
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(providerName, userAttributes);
        SocialPlatform socialPlatform = getSocialPlatform(providerName);

        String providerId = oAuth2UserInfo.getProviderId();
        String nickName = oAuth2UserInfo.getNickName();

        List<User> userEntity = userRetriever.getUsersBySocialId(providerId);

        if(userEntity==null || isWithdrawn(userEntity)){
            return saveUser(nickName, providerId, socialPlatform, tokenRequest, refreshToken);
        }

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
        throw new UserException(INVALID_SOCIAL_PLATFORM);
    }

    private SocialPlatform getSocialPlatform(String providerName) {
        try {
            return switch (providerName) {
                case "kakao" -> KAKAO;
                case "apple" -> APPLE;
                default -> throw new UserException(INVALID_SOCIAL_PLATFORM);
            };
        } catch (FeignException e){
            throw new UserException(INVALID_SOCIAL_PLATFORM);
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

        tokenRedisRetriever.saveBlockedToken(accessToken);
        tokenRedisRetriever.deleteRefreshToken(refreshToken);
    }


    @Transactional
    public void signout(Long userId) {

        User user = userRetriever.getUserById(userId);
        // List<User> sameSocialUsers = userRetriever.getUsersBySocialId(user.getSocialId());  // 동일한 소셜 계정으로 탈퇴-가입을 반복한 경우
        tokenRedisRetriever.deleteRefreshToken(user.getRefreshToken());
        userManager.withdrawUser(user);
        healthRetriever.deleteByUser(user);   // 온보딩 건강 정보 삭제
        user.getUserMissionChoice().forEach(umc -> {
            umc.deleteUser();
            userMissionChoicesRetriever.deleteById(umc.getId());
        });
        user.setUserMissionChoiceToNull();

        Parentchild parentchild = user.getParentchild();
        List<User> users = userRetriever.getUsersByParentchild(parentchild);

        boolean allUsersDeleted = users.stream()
            .allMatch(u -> u.getSocialPlatform().equals(WITHDRAW));
        log.info("2명의 유저 모두 탈퇴? {}", allUsersDeleted);
        if (allUsersDeleted) {
            log.info("User Size: {}", users.size());
            if (users.size() == 1) {
                log.info("삭제된 유저: {}", users.get(0).getNickname());
            } else if (users.size() == 2) {
                log.info("삭제된 부모자식: {} X {}", users.get(0).getNickname(), users.get(1).getNickname());
            }
            users.forEach(u -> {
                u.getUserMissions().forEach(um -> {
                    um.deleteUser();
                    userMissionRetriever.deleteById(um.getId());
                });
                u.setUserMissionToNull();
                u.setParentchildToNull();
            });

            parentchildRetriever.deleteById(parentchild.getId());
            users.forEach(u -> userRetriever.deleteById(u.getId()));
        }
    }


    // TODO 혜연 언니한테 질문
    private void sendRevokeRequest(String data, SocialPlatform socialPlatform, String accessToken) {
        // String appleRevokeUrl = "https://appleid.apple.com/auth/revoke"; //TODO 추후 애플 로그인 구현 후
        String kakaoRevokeUrl = "https://kapi.kakao.com/v1/user/unlink";

        RestTemplate restTemplate = new RestTemplate();
        String revokeUrl = "";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(data, headers);

        switch (socialPlatform) {
            // case APPLE -> revokeUrl = appleRevokeUrl;
            case KAKAO -> {
                revokeUrl = kakaoRevokeUrl;
                headers.setBearerAuth(accessToken);
            }
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(revokeUrl, HttpMethod.POST, entity, String.class);

        log.info("response="+responseEntity);
    }
}