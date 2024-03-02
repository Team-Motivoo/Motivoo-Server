package sopt.org.motivoo.domain.mission.repository;

import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;
import sopt.org.motivoo.domain.mission.exception.MissionException;
import sopt.org.motivoo.domain.user.entity.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMissionChoicesRetriever {

	private final UserMissionChoicesRepository userMissionChoicesRepository;
	private final UserMissionChoicesJdbcRepository userMissionChoicesJdbcRepository;

	public UserMissionChoices save(UserMissionChoices missionChoices) {
		return userMissionChoicesRepository.save(missionChoices);
	}

	public void saveAll(List<UserMissionChoices> missionChoices) {
		userMissionChoicesRepository.saveAll(missionChoices);
	}

	public void deleteByUser(User user) {
		userMissionChoicesRepository.deleteAllByUser(user);
	}

	public List<UserMissionChoices> getUserMissionChoice(User user) {
		List<UserMissionChoices> choices = userMissionChoicesRepository.findAllByUserAndCreatedAt(user, LocalDate.now());
		if (!choices.isEmpty() && choices.size() != 2) {
			log.info("choices의 날짜와 개수: {}-{}", choices.get(0).getCreatedAt(), choices.size());
			throw new MissionException(INVALID_USER_MISSION_CHOICES);
		}
		return choices;
	}

	public boolean existsByUser(User user) {
		return userMissionChoicesRepository.existsByUserAndCreatedAt(user, LocalDate.now());
	}

	public void bulkSaveUserMissionChoices(List<User> users, LocalDate date, Mission mission) {
		userMissionChoicesJdbcRepository.bulkChoicesSave(users, date, mission);
	}
}
