package sopt.org.motivoo.domain.mission.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;
import sopt.org.motivoo.domain.user.entity.User;

public interface UserMissionChoicesRepository extends JpaRepository<UserMissionChoices, Long> {

    void deleteByUser(User user);

    @Query("SELECT umc FROM UserMissionChoices umc WHERE umc.user = :user AND DATE(umc.createdAt) = DATE(:date)")
    List<UserMissionChoices> findAllByUserAndCreatedAt(User user, LocalDate date);

    @Query("SELECT COUNT(umc.id) > 0 FROM UserMissionChoices umc WHERE umc.user=:user AND DATE(umc.createdAt) = DATE(:date)")
    boolean existsByUserAndCreatedAt(User user, LocalDate date);
}
