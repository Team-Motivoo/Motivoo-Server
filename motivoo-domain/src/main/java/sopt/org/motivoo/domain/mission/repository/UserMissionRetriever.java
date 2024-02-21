package sopt.org.motivoo.domain.mission.repository;

import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;
import sopt.org.motivoo.domain.mission.exception.MissionException;
import sopt.org.motivoo.domain.user.entity.User;

@Component
@RequiredArgsConstructor
public class UserMissionRetriever {

	private final UserMissionRepository userMissionRepository;

	public List<UserMission> getUserMissionsByCreatedDt(LocalDate date) {
		return userMissionRepository.findUserMissionsByCreatedAt(date);
	}

	public List<UserMission> getUserMissionsByCreatedAtBefore(LocalDateTime dateTime) {
		return userMissionRepository.findUserMissionsByCreatedAtBefore(dateTime);
	}

	public UserMission getUserMission(Long userMissionId) {
		return userMissionRepository.findById(userMissionId).orElseThrow(
			() -> new MissionException(USER_MISSION_NOT_FOUND));
	}

	public void saveUserMission(UserMission userMission) {
		userMissionRepository.save(userMission);
	}
	
	public void saveAll(List<UserMission> userMissions) {
		userMissionRepository.saveAll(userMissions);
	}

	public void deleteByUser(User user) {
		userMissionRepository.deleteByUser(user);
	}
}
