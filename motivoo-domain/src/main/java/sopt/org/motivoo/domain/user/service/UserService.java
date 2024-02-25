package sopt.org.motivoo.domain.user.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.auth.repository.TokenRedisRepository;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.repository.HealthRetriever;
import sopt.org.motivoo.domain.user.dto.response.MyHealthInfoResult;
import sopt.org.motivoo.domain.user.dto.response.MyPageInfoResult;
import sopt.org.motivoo.domain.user.entity.SocialPlatform;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.repository.UserRetriever;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRetriever userRetriever;
	private final HealthRetriever healthRetriever;
	private final TokenRedisRepository tokenRedisRepository;

	private final UserManager userManager;

	public MyPageInfoResult getMyInfo(final Long userId) {
		User user = userRetriever.getUserById(userId);
		return MyPageInfoResult.of(user);
	}

	public MyHealthInfoResult getMyHealthInfo(final Long userId) {
		Health health = healthRetriever.getHealthByUserId(userId);
		return MyHealthInfoResult.of(health);
	}


	@Transactional
	public void deleteSocialAccount(Long userId) {

		User user = userRetriever.getUserById(userId);
		List<User> sameSocialUsers = userRetriever.getUsersBySocialId(user.getSocialId());  // 동일한 소셜 계정으로 탈퇴-가입을 반복한 경우
		userManager.deleteKakaoAccount(sameSocialUsers);
		healthRetriever.deleteByUser(user);   // 온보딩 건강 정보 삭제

		//TODO 애플 로그인 구현 후 리팩토링
/*		switch (){
			case "apple" ->
			case "kakao" -> deleteKakaoAccount(userId, accessToken);
		}*/
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
