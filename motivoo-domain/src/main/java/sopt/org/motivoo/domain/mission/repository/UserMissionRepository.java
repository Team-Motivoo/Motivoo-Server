package sopt.org.motivoo.domain.mission.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sopt.org.motivoo.domain.mission.entity.CompletedStatus;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.mission.entity.MissionQuest;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.user.entity.User;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

	//== READ ==//
	List<UserMission> findUserMissionsByUserOrderByCreatedAt(User user);

	Optional<UserMission> findFirstByUserOrderByCreatedAt(User user);

	// Scheduler를 통해 DB에 저장된 오늘의 미션 리스트 전부 가져오기
	@Query("SELECT um FROM UserMission um WHERE DATE(um.createdAt) = DATE(:date)")
	List<UserMission> findUserMissionsByCreatedAt(LocalDate date);

	@Query("SELECT um FROM UserMission um WHERE um.createdAt < :date")
	List<UserMission> findUserMissionsByCreatedAtBefore(@Param("date") LocalDateTime date);

	boolean existsByUser(User user);


	//== DELETE ==//
	void deleteAllByUser(User user);


	//== UPDATE ==//
	@Modifying
	@Query("UPDATE UserMission um SET um.mission = :mission, um.missionQuest = :quest, um.completedStatus = :status WHERE um.user = :user AND DATE(um.createdAt) = DATE(:date)")
	void updateValidTodayMission(Mission mission, MissionQuest quest, CompletedStatus status, User user, LocalDate date);
}

