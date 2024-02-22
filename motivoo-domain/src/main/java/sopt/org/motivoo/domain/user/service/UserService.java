package sopt.org.motivoo.domain.user.service;

import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

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
import sopt.org.motivoo.domain.user.exception.UserException;
import sopt.org.motivoo.domain.user.repository.UserRetriever;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRetriever userRetriever;
	private final HealthRetriever healthRetriever;
	private final TokenRedisRepository tokenRedisRepository;

	public MyPageInfoResult getMyInfo(final Long userId) {
		User user = userRetriever.getUserById(userId);
		return MyPageInfoResult.of(user);
	}

	public MyHealthInfoResult getMyHealthInfo(final Long userId) {
		Health health = healthRetriever.getHealthByUserId(userId);
		return MyHealthInfoResult.of(health);
	}


	@Transactional
	public void deleteSocialAccount(Long userId){
		deleteKakaoAccount(userId);

		//TODO 애플 로그인 구현 후 리팩토링
//		switch (){
//			case "apple" ->
//			case "kakao" -> deleteKakaoAccount(userId, accessToken);
//		}
	}

	@Transactional
	public void deleteKakaoAccount(Long userId){
		User user = userRetriever.getUserById(userId);
		//탈퇴하려고 하는데 가입한 이력이 없는 경우 -> 30일이 지나서 영구탈퇴
		if(user==null)
			throw new UserException(ALREADY_WITHDRAW_USER);

		String socialId = user.getSocialId();
		log.info("socialId= "+socialId);
		List<User> users = userRetriever.getUsersBySocialId(socialId);
		boolean is_withdrawn = users.stream()
				.filter(u -> !u.isDeleted())
				.peek(u -> {
					u.udpateDeleted();
					u.updateDeleteAt();
					healthRetriever.deleteByUser(u);
					//온보딩 정보 지우기
					log.info("유저 정보=" + u.isDeleted() + " " + u.getDeletedAt());
					String accessToken = userRetriever.getAccessTokenById(u.getId());
					String refreshToken = userRetriever.getRefreshTokenById(u.getId());
					u.updateRefreshToken(null);
				})
				.findFirst()
				.isPresent();

		//이미 탈퇴한 경우
		if(!is_withdrawn)
			throw new UserException(ALREADY_WITHDRAW_USER);
		//sendRevokeRequest(null, KAKAO, "FHdk_lLY2GObLpez2qGCdmqDlMBwwDk7FXgKPXTZAAABjRVYY2X6Fwx8Dt1GgQ"); //카카오 연결 해제
	}

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
