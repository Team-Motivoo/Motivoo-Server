package sopt.org.motivooServer.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sopt.org.motivooServer.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    User findBySocialId(String socialId);
    @Query("select u.refreshToken from User u where u.id=?1")
    String findRefreshTokenById(Long id);
}
