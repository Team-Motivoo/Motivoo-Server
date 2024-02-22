package sopt.org.motivoo.domain.mission.repository;

import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.mission.entity.MissionQuest;
import sopt.org.motivoo.domain.mission.exception.MissionException;

@Component
@RequiredArgsConstructor
public class MissionQuestRetriever {

	private final MissionQuestRepository missionQuestRepository;

	public MissionQuest getRandomMissionQuest() {
		MissionQuest missionQuest = missionQuestRepository.findRandomMissionQuest();
		if (missionQuest == null) {
			throw new MissionException(MISSION_QUEST_NOT_FOUND);
		}
		return missionQuest;
	}
}
