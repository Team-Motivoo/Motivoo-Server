package sopt.org.motivooServer.domain.user.service;

import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.*;
import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.health.entity.Health;
import sopt.org.motivooServer.domain.health.exception.HealthException;
import sopt.org.motivooServer.domain.health.repository.HealthRepository;
import sopt.org.motivooServer.domain.user.dto.response.MyHealthInfoResponse;
import sopt.org.motivooServer.domain.user.dto.response.MyPageInfoResponse;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final HealthRepository healthRepository;

	public MyPageInfoResponse getMyPage(final Long userId) {
		User user = getUserById(userId);
		return MyPageInfoResponse.ofMain(user);
	}

	public MyPageInfoResponse getMyInfo(final Long userId) {
		User user = getUserById(userId);
		return MyPageInfoResponse.of(user);
	}

	public MyHealthInfoResponse getMyHealthInfo(final Long userId) {
		Health health = healthRepository.findByUserId(userId).orElseThrow(
			() -> new HealthException(NOT_EXIST_USER_HEALTH)  // TODO UserException과 HealthException을 굳이 분리해야 할까?
		);
		return MyHealthInfoResponse.of(health);
	}

	private User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			() -> new UserException(USER_NOT_FOUND)
		);
	}
}
