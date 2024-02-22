package sopt.org.motivoo.domain.health.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.user.entity.User;

public interface HealthRepository extends JpaRepository<Health, Long> {
    Optional<Health> findByUser(User user);
	Optional<Health> findByUserId(Long userId);  // TODO primitive? (null에 대한 안전성 보장)
    void deleteByUser(User user);
}