package sopt.org.motivooServer.domain.health.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.motivooServer.domain.health.entity.Health;
import sopt.org.motivooServer.domain.user.entity.User;

import java.util.Optional;

public interface HealthRepository extends JpaRepository<Health, Long> {

    Optional<Health> findByUser(User user);
}
