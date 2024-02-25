package sopt.org.motivoo.domain.health.repository;

import static sopt.org.motivoo.domain.health.exception.HealthExceptionType.*;

import java.util.Optional;

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

	// 유저의 건강정보 조회 (주의사항 반영 의도)
	public Health getHealthByUser(User user) {
		return healthRepository.findByUser(user).orElseThrow(
			() -> new HealthException(HEALTH_NOT_FOUND));
	}

	public void deleteByUser(User user) {
		healthRepository.deleteByUser(user);
	}

	public void validateHealthByUser(User user) {
		if (healthRepository.findByUser(user).isPresent()) {
			throw new HealthException(EXIST_ONBOARDING_INFO);
		}
	}

	public boolean existsHealthByUser(User user) {
		return healthRepository.findByUser(user).isPresent();
	}

	public Health save(Health health) {
		return healthRepository.save(health);
	}
}
