package sopt.org.motivooServer.domain.mission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivooServer.domain.mission.entity.Mission;

public interface MissionRepository extends JpaRepository<Mission, Long> {

	List<Mission> findMissionsByTarget(String target);

}
