package sopt.org.motivooServer.domain.mission.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.user.entity.User;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

	List<UserMission> findUserMissionsByUserOrderByCreatedAt(User user);

	Optional<UserMission> findFirstByUserOrderByCreatedAt(User user);

	// Scheduler를 통해 DB에 저장된 오늘의 미션 리스트 전부 가져오기
	@Query("SELECT um FROM UserMission um WHERE DATE(um.createdAt) = DATE(:date)")
	List<UserMission> findUserMissionsByCreatedAt(LocalDate date);
}
