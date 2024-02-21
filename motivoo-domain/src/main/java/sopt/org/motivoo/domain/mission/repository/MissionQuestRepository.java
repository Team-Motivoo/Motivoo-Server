package sopt.org.motivoo.domain.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sopt.org.motivoo.domain.mission.entity.MissionQuest;

public interface MissionQuestRepository extends JpaRepository<MissionQuest, Long> {

	@Query(value = "SELECT * FROM mission_quest ORDER BY RAND() LIMIT 1", nativeQuery = true)
	MissionQuest findRandomMissionQuest();
}
