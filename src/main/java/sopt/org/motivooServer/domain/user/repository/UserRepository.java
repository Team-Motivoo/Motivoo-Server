package sopt.org.motivooServer.domain.user.repository;


import org.springframework.data.repository.Repository;
import sopt.org.motivooServer.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    void save(User user);
    Optional<User> findById(Long id);

    User findBySocialId(String socialId);

}
