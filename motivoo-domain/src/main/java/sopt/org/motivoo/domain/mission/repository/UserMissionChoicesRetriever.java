package sopt.org.motivoo.domain.mission.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;
import sopt.org.motivoo.domain.user.entity.User;

@Component
@RequiredArgsConstructor
public class UserMissionChoicesRetriever {

	private final UserMissionChoicesRepository userMissionChoicesRepository;

	public UserMissionChoices save(UserMissionChoices missionChoices) {
		return userMissionChoicesRepository.save(missionChoices);
	}

	public void deleteByUser(User user) {
		userMissionChoicesRepository.deleteByUser(user);
	}
}
