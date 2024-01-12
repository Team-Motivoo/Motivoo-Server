package sopt.org.motivooServer.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;

import sopt.org.motivooServer.domain.user.entity.User;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    User findBySocialId(String socialId);
    @Query("select u.refreshToken from User u where u.id=?1")
    String findRefreshTokenById(Long id);

    @Query("select count(u.id) from User u where u.parentchild=?1")
    int countByParentchild(Parentchild parentchild);

    @Query("select u.id from User u where u.parentchild=?1 and u.id!=?2")
    Long getOpponentId(Parentchild parentchild, Long id);


    @Query("select u from User u where u.parentchild = ?1 and u.deleted=true")
    List<User> findByParentchild(Parentchild parentchild);

    @Modifying
    @Query("delete from User u where u.deletedAt < now() and u.deleteExpired=true")
    void deleteExpiredUser();

    @Query("select u from User u where u.id!=?1 and u.parentchild=?2")
    Optional<User> findByIdAndParentchild(Long userId, Parentchild parentchild);
}
