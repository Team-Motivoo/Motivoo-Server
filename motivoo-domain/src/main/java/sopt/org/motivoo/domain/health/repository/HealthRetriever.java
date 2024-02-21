package sopt.org.motivoo.domain.health.repository;

import static sopt.org.motivoo.domain.health.exception.HealthExceptionType.*;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.exception.HealthException;
import sopt.org.motivoo.domain.user.entity.User;

@Component
@RequiredArgsConstructor
public class HealthRetriever {

	private final HealthRepository healthRepository;

	public Health getHealthByUserId(Long userId) {
		return healthRepository.findByUserId(userId).orElseThrow(
			() -> new HealthException(NOT_EXIST_USER_HEALTH));
	}

	public void deleteByUser(User u) {
		healthRepository.deleteByUser(u);
	}
}
