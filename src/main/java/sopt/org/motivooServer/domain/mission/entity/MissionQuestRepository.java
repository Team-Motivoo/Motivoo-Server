package sopt.org.motivooServer.domain.mission.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MissionQuestRepository extends JpaRepository<MissionQuest, Long> {

	@Query(value = "SELECT * FROM mission_quest ORDER BY RAND() LIMIT 1", nativeQuery = true)
	MissionQuest findRandomMissionQuest();
}
