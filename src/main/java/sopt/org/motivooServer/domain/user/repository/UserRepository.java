package sopt.org.motivooServer.domain.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;

import sopt.org.motivooServer.domain.user.entity.SocialPlatform;
import sopt.org.motivooServer.domain.user.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User findBySocialId(String socialId);

    @Query("select u.refreshToken from User u where u.id=?1")
    String findRefreshTokenById(Long id);

    @Query("select u.socialAccessToken from User u where u.id=?1")
    String findSocialAccessTokenById(Long id);

    @Query("select u from User u where u.deletedAt < now()")
    List<User> deleteExpiredUser();

    @Query("select count(u.id) from User u where u.parentchild=?1")
    int countByParentchild(Parentchild parentchild);

    @Query("select u.id from User u where u.parentchild=?1 and u.id!=?2")
    Long getOpponentId(Parentchild parentchild, Long id);

    @Query("select u from User u where u.parentchild = ?1 and u.deleted=true")
    List<User> findByParentchild(Parentchild parentchild);

    @Query("select u from User u where u.id!=?1 and u.parentchild=?2")
    Optional<User> findByIdAndParentchild(Long userId, Parentchild parentchild);

    @Modifying
    @Query("update User u set u.deletedAt=?1 where u.id=?2")
    void updateDeleteAt(LocalDateTime deletedAt, Long userId);

    @Query("UPDATE User u SET u.deleted=true WHERE u.id=?1")
    void updateDelete(Long userId);

    boolean existsBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);

    List<User> findBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);

    @Query("select u from User u where u.id=?1 and u.deleted=false")
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findAllByIds(@Param("ids") List<Long> ids);

    List<User> findAllByDeleted(boolean deleted);
}
