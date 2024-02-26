package sopt.org.motivoo.domain.mission.repository;

import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.mission.exception.MissionException;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.entity.UserType;

@Component
@RequiredArgsConstructor
public class MissionRetriever {

	private final MissionRepository missionRepository;

	@NotNull
	public List<Mission> getAllMissions() {
		return missionRepository.findAll();
	}

	public void updateIconUrl(String iconUrl) {
		missionRepository.updateIcon(iconUrl);
	}

	public void saveAll(List<Mission> missions) {
		missionRepository.saveAll(missions);
	}

	// 미션 히스토리 - 상대방의 오늘의 미션 미선정 시
	public Mission getEmptyMission() {
		return missionRepository.findMissionsByTarget(UserType.NONE).get(0);
	}

	public List<Mission> getMissionsByTarget(User user) {
		return missionRepository.findMissionsByTarget(user.getType());
	}

	public Mission getMissionById(Long missionId) {
		return missionRepository.findById(missionId).orElseThrow(
			() -> new MissionException(MISSION_NOT_FOUND));
	}

	public void updateStepCount(int stepCount, Mission mission) {
		missionRepository.updateStepCountById(stepCount, mission.getId());
	}
}
