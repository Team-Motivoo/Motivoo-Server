package sopt.org.motivoo.domain.mission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.user.entity.UserType;

public interface MissionRepository extends JpaRepository<Mission, Long> {

	List<Mission> findMissionsByTarget(UserType target);

	@Modifying
	@Query("update Mission m set m.iconUrl=?1 where m.iconUrl = ''")
	void updateIcon(String iconUrl);

	@Modifying
	@Query("update Mission m set m.stepCount = :stepCount where m.id = :id")
	void updateStepCountById(int stepCount, Long id);
}
