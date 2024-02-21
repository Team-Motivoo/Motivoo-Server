package sopt.org.motivoo.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.user.entity.SocialPlatform;
import sopt.org.motivoo.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.socialId=?1 and u.deleted = false")
    List<User> findBySocialId(String socialId);


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

    @Query("select u from User u where u.parentchild = ?1 and u.deleted=false")
    List<User> findUserByParentchild(Parentchild parentchild);

    @Query("select u from User u where u.parentchild.id = ?1 and u.deleted=false")
    List<User> findUsersByParentchildId(Long parentchildId);

    @Query("select u from User u where u.id!=?1 and u.parentchild=?2")
    Optional<User> findByIdAndParentchild(Long userId, Parentchild parentchild);

    boolean existsBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);

    @Query("select u from User u where u.deleted=true and u.id=?1") //탈퇴한 이력이 있는 회원
    User findByDeletedAndId(Long userId);

    @Query("select u from User u where u.id=?1 and u.deleted=false")
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findAllByIds(@Param("ids") List<Long> ids);

    List<User> findAllByDeleted(boolean deleted);

}