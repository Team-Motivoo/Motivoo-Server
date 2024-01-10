package sopt.org.motivooServer.domain.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivooServer.domain.mission.entity.UserMission;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
}
