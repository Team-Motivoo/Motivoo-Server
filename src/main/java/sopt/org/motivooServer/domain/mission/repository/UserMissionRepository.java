package sopt.org.motivooServer.domain.mission.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.user.entity.User;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

	List<UserMission> findUserMissionsByUserOrderByCreatedAt(User user);

	Optional<UserMission> findFirstByUserOrderByCreatedAt(User user);


}
