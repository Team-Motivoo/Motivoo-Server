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

	public MyPageInfoResult getMyInfo(final Long userId) {
		User user = userRetriever.getUserById(userId);
		return MyPageInfoResult.of(user);
	}

	public MyHealthInfoResult getMyHealthInfo(final Long userId) {
		Health health = healthRetriever.getHealthByUserId(userId);
		return MyHealthInfoResult.of(health);
	}

}
