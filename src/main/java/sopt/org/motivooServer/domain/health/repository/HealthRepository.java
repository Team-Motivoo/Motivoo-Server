package sopt.org.motivooServer.domain.health.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivooServer.domain.health.entity.Health;

public interface HealthRepository extends JpaRepository<Health, Long> {

	Optional<Health> findByUserId(Long userId);
}
