package sopt.org.motivooServer.domain.user.service;

import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.*;
import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;
import sopt.org.motivooServer.domain.auth.repository.TokenRedisRepository;
import sopt.org.motivooServer.domain.health.entity.Health;
import sopt.org.motivooServer.domain.health.exception.HealthException;
import sopt.org.motivooServer.domain.health.repository.HealthRepository;
import sopt.org.motivooServer.domain.user.dto.response.MyHealthInfoResponse;
import sopt.org.motivooServer.domain.user.dto.response.MyPageInfoResponse;
import sopt.org.motivooServer.domain.user.entity.SocialPlatform;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final HealthRepository healthRepository;
	private final TokenRedisRepository tokenRedisRepository;

	public MyPageInfoResponse getMyInfo(final Long userId) {
		User user = getUserById(userId);
		return MyPageInfoResponse.of(user);
	}

	public MyHealthInfoResponse getMyHealthInfo(final Long userId) {
		Health health = healthRepository.findByUserId(userId).orElseThrow(
			() -> new HealthException(NOT_EXIST_USER_HEALTH)
		);
		return MyHealthInfoResponse.of(health);
	}

	private User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			() -> new UserException(USER_NOT_FOUND)
		);
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
		User user = getUserById(userId);
		user.udpateDeleted(); //회원 탈퇴 deleted false -> true
		user.updateDeleteAt(); //회원 탈퇴날짜 갱신

		System.out.println("유저 정보=" + user.isDeleted()+" " +user.getDeletedAt());
		String accessToken = userRepository.findSocialAccessTokenById(user.getId());
		String refreshToken = userRepository.findRefreshTokenById(user.getId());
		System.out.println("토큰 정보=" + accessToken+" "+refreshToken);
		user.updateRefreshToken(null);

		//sendRevokeRequest(null, KAKAO, "FHdk_lLY2GObLpez2qGCdmqDlMBwwDk7FXgKPXTZAAABjRVYY2X6Fwx8Dt1GgQ"); //카카오 연결 해제
	}

	private void sendRevokeRequest(String data, SocialPlatform socialPlatform, String accessToken) {
		String appleRevokeUrl = "https://appleid.apple.com/auth/revoke"; //TODO 추후 애플 로그인 구현 후
		String kakaoRevokeUrl = "https://kapi.kakao.com/v1/user/unlink";

		RestTemplate restTemplate = new RestTemplate();
		String revokeUrl = "";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<String> entity = new HttpEntity<>(data, headers);

		switch (socialPlatform) {
			case APPLE -> revokeUrl = appleRevokeUrl;
			case KAKAO -> {
				revokeUrl = kakaoRevokeUrl;
				headers.setBearerAuth(accessToken);
			}
		}

		ResponseEntity<String> responseEntity = restTemplate.exchange(revokeUrl, HttpMethod.POST, entity, String.class);

		log.info("response="+responseEntity);
	}

}
