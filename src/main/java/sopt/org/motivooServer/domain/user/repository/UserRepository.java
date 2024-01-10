package sopt.org.motivooServer.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;
import sopt.org.motivooServer.domain.user.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User findBySocialId(String socialId);
    @Query("select u.refreshToken from User u where u.id=?1")
    String findRefreshTokenById(Long id);

    @Query("select u from User u where u.id!=?1 and u.parentchild=?2")
    Optional<User> findByIdAndParentchild(Long userId, Parentchild parentchild);
}
