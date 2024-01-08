package sopt.org.motivooServer.domain.user.repository;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import sopt.org.motivooServer.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    void save(User user);
    Optional<User> findById(Long id);

    Optional<User> findBySocialId(String socialId);

    @Query("select u.socialRefreshToken from User u where u.id=?1")
    String findRefreshTokenById(Long id);


}
