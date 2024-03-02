package sopt.org.motivoo.domain.mission.service;

import static sopt.org.motivoo.domain.mission.entity.CompletedStatus.*;
import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;
import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.health.entity.ExerciseLevel;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.entity.HealthNote;
import sopt.org.motivoo.domain.mission.dto.response.OpponentGoalStepsResult;
import sopt.org.motivoo.domain.mission.dto.response.StepStatusResult;
import sopt.org.motivoo.domain.mission.entity.CompletedStatus;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.mission.entity.MissionQuest;
import sopt.org.motivoo.domain.mission.entity.MissionType;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;
import sopt.org.motivoo.domain.mission.exception.MissionException;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.exception.UserException;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMissionManager {

	private static final int MAX_MISSION_CHOICES = 2;

	@Transactional
	public void updateMissionSuccess(UserMission todayMission, final String imgUrl) {

		checkMissionStepCompleted(todayMission);

		todayMission.updateImgUrl(imgUrl);
		todayMission.updateCompletedStatus(CompletedStatus.SUCCESS);
	}

	public StepStatusResult updateStepStatusResult(User myUser, User opponentUser, int myStep, int opponentStep) {
		int myGoalStep = 0;
		int opponentGoalStep = 0;

		log.info("현재 접속한 유저 - {} X 나와 매칭된 부모자녀 유저 - {}", myUser.getNickname(), opponentUser.getNickname());

		boolean myUserMissionsEmpty = myUser.getUserMissions().isEmpty();
		boolean opponentUserMissionsEmpty = opponentUser.getUserMissions().isEmpty();

		if (myUserMissionsEmpty && opponentUserMissionsEmpty) {
			return StepStatusResult.of(myUser, opponentUser, myGoalStep, opponentGoalStep, false, false);
		}

		if (!opponentUserMissionsEmpty) {
			UserMission opponentCurrentUserMission = opponentUser.getCurrentUserMission();
			opponentGoalStep = (opponentCurrentUserMission != null && validateTodayDateMission(opponentCurrentUserMission)) ? opponentCurrentUserMission.getMission().getStepCount() : 0;
			assert opponentCurrentUserMission != null;
			isStepCountCompleted(opponentStep, opponentCurrentUserMission);
		}

		if (!myUserMissionsEmpty) {
			UserMission todayMission = myUser.getCurrentUserMission();
			if (!validateTodayDateMission(todayMission)) {
				return StepStatusResult.of(myUser, opponentUser, myGoalStep, opponentGoalStep, false, false);
			}
			myGoalStep = todayMission.getMission().getStepCount();
			boolean stepCountCompleted = isStepCountCompleted(myStep, myUser.getCurrentUserMission());

			return StepStatusResult.of(myUser, opponentUser, myGoalStep, opponentGoalStep, stepCountCompleted,
				todayMission.getImgUrl() != null);
		}

		return StepStatusResult.of(myUser, opponentUser, myGoalStep, opponentGoalStep, false, false);
	}

	private boolean isStepCountCompleted(int currentStepCount, UserMission todayMission) {
		boolean isStepCountCompleted = currentStepCount >= todayMission.getMission().getStepCount();
		if (isStepCountCompleted) {
			todayMission.updateCompletedStatus(STEP_COMPLETED);
		}
		return isStepCountCompleted;
	}

	// 오늘의 미션 걸음 수 달성 상태 확인
	private void checkMissionStepCompleted(UserMission todayMission) {
		if (!todayMission.getCompletedStatus().equals(STEP_COMPLETED)) {
			throw new MissionException(NOT_COMPLETE_MISSION_STEPS_SUCCESS);
		}
	}

	@NotNull
	public List<Mission> getFilteredMissions(List<Mission> missions, Health health) {
		final List<Mission> missionFiltered = new ArrayList<>();

		Set<HealthNote> userNotes = health.getHealthNotes();
		ExerciseLevel exerciseLevel = health.getExerciseLevel();

		log.info("Mission 필터링 시작!");
		for (Mission mission : missions) {
			Set<HealthNote> missionNotes = HealthNote.of(mission.getHealthNotes());
			boolean hasUserNotes = missionNotes.stream().anyMatch(userNotes::contains);
			boolean hasExerciseLevel = MissionType.of(mission.getType()).containsLevel(exerciseLevel);

			if (!hasUserNotes && !hasExerciseLevel) {
				missionFiltered.add(mission);
			}
		}
		log.info("맞춤 Mission 리스트에 추가(Shuffle 전): {}가지", missionFiltered.size());
		Collections.shuffle(missionFiltered);

		return missionFiltered;
	}

	private List<UserMissionChoices> filterTodayUserMissionV2(List<Mission> missions, Health health, User user) {

		// 부모 미션 or 자식 미션 리스트
		Set<HealthNote> userNotes = health.getHealthNotes();
		ExerciseLevel exerciseLevel = health.getExerciseLevel();

		return missions.parallelStream()
			.filter(mission -> {
				Set<HealthNote> missionNotes = HealthNote.of(mission.getHealthNotes());
				boolean hasUserNotes = missionNotes.stream().anyMatch(userNotes::contains);
				boolean hasExerciseLevel = MissionType.of(mission.getType()).containsLevel(exerciseLevel);
				return !hasUserNotes && !hasExerciseLevel;
			})
			.limit(MAX_MISSION_CHOICES)
			.map(mission -> UserMissionChoices.builder()
				.mission(mission)
				.user(user).build())
			// .map(userMissionChoicesRetriever::save)
			.toList();
	}

	@NotNull
	public List<UserMissionChoices> getFilteredMissionChoices(User user, List<Mission> missionChoicesFiltered) {

		List<UserMissionChoices> missionChoices = new ArrayList<>();

		for (int i = 0; i < Math.min(missionChoicesFiltered.size(), 2); i++) {
			UserMissionChoices missionChoice = UserMissionChoices.builder()
				.mission(missionChoicesFiltered.get(i))
				.user(user).build();
			missionChoice.setCreatedAtNow(LocalDateTime.now());
			missionChoices.add(missionChoice);
		}

		// user.setPreUserMissionChoice(missionChoices);
		// if (missionChoices.isEmpty() || missionChoices.size() == 1) {
		// 	throw new MissionException(NOT_FILTERED_TODAY_MISSION);
		// }
		return missionChoices;
	}

	@NotNull
	public UserMission createEmptyUserMission(User user, LocalDate date, Mission mission, MissionQuest quest) {
		UserMission um = UserMission.builderForEmpty()
			.completedStatus(CompletedStatus.NONE)
			.user(user)
			.mission(mission)
			.missionQuest(quest).build();

		um.updateCreatedAt(date.atStartOfDay());  // 동일한 날짜로 세팅
		user.addUserMission(um);
		return um;
	}

	/*
	// TODO Batch Test 후 삭제
	@NotNull
	public List<UserMission> createEmptyDateMissions(List<User> users) {
		List<UserMission> emptyUserMissions = new ArrayList<>();

		// 두 유저가 가진 모든 UserMission의 createdAt 날짜 집합
		Set<LocalDate> userDates = new HashSet<>();
		users.forEach(user -> {
			userDates.addAll(user.getUserMissions().stream()
				.map(userMission -> userMission.getCreatedAt().toLocalDate())
				.collect(Collectors.toSet()));
			log.info("userDates.size(): {}", userDates.size());
		});

		users.forEach(user -> {
			// 각 유저가 가진 날짜 집합
			Set<LocalDate> myDates = user.getUserMissions().stream()
				.map(userMission -> userMission.getCreatedAt().toLocalDate())
				.collect(Collectors.toSet());

			// userDates과 myDates를 비교하여 없는 날짜에는 빈 값의 UserMission 생성해주기
			userDates.forEach(date -> {
				if (!myDates.contains(date)) {
					UserMission um = createEmptyUserMission(user, LocalDate.now());
					emptyUserMissions.add(um);
				}
			});
		});
		return emptyUserMissions;
	}*/

	@NotNull
	public Map<LocalDate, List<UserMission>> sortMissionsByDate(List<User> users) {

		List<UserMission> myMissions = users.get(0).getUserMissions();
		List<UserMission> opponentMissions = users.get(1).getUserMissions();
		int totalMissionSize = myMissions.size();

		Map<LocalDate, List<UserMission>> dateGroups = IntStream.range(0, totalMissionSize)
			.filter(i ->
				// 1. 둘 다 선택하지 않은 경우
				!( myMissions.get(i).isEmptyUserMission() && opponentMissions.get(i).isEmptyUserMission() )
					// 2. 날짜가 동일하지 않은 경우
					&& myMissions.get(i).getCreatedAt().toLocalDate().equals(opponentMissions.get(i).getCreatedAt().toLocalDate()))
			.boxed()
			.collect(Collectors.toMap(i -> users.get(0).getUserMissions().get(i).getCreatedAt().toLocalDate(),
				i -> List.of( myMissions.get(i), opponentMissions.get(i)), (existing, replacement) -> existing));

		Map<LocalDate, List<UserMission>> sortedGroups = new TreeMap<>(Comparator.reverseOrder());
		sortedGroups.putAll(dateGroups);

		return sortedGroups;
	}

	@NotNull
	public Map<LocalDate, List<UserMission>> getMissionDateGroup(Map<LocalDate, List<UserMission>> missionsByDate) {
		log.info("missionsByDate size: {}", missionsByDate.size());
		for (LocalDate localDateTime : missionsByDate.keySet()) {
			log.info("missionsByDate.get(localDateTime) size: {}-{}", missionsByDate.get(localDateTime).size(), localDateTime);

			// 인증에 성공하지 못한 UserMission 이미지 Null로 처리
			/*missionsByDate.get(localDateTime).stream()
				.filter(um -> um.getImgUrl() != null)
				.forEach(um -> {
					try {
						String imgUrl = s3Service.getURL(MISSION_PREFIX.value() + um.getImgUrl());
						log.info("S3에서 받아온 이미지 URL: {}", imgUrl);
						// um.updateImgUrl(imgUrl);
					} catch (IllegalArgumentException | BusinessException e) {
						log.error(e.getMessage());
						um.updateImgUrl(null);
					}
				});*/

			if (!localDateTime.equals(LocalDate.now())) {
				missionsByDate.get(localDateTime).stream()
					.filter(um -> um.getImgUrl() == null && !um.isEmptyUserMission())
					.forEach(um -> um.updateCompletedStatus(FAIL));
			}
			missionsByDate.get(localDateTime).stream()
				.filter(um -> um.getImgUrl() != null && !um.isEmptyUserMission())
				.forEach(um -> um.updateCompletedStatus(SUCCESS));

			missionsByDate.get(localDateTime).stream()
				.filter(UserMission::isEmptyUserMission)
				.forEach(um -> um.updateCompletedStatus(NONE));

			// log.info("key={}, value={} 🥹{}", localDateTime, missionsByDate.get(localDateTime).get(0).getMission().getContent(),
			// 	missionsByDate.get(localDateTime).get(1).getMission().getContent());
		}
		return missionsByDate;
	}

	@NotNull
	public UserMission createTodayUserMission(Mission mission, User user, MissionQuest quest) {

		UserMission userMission = UserMission.builder()
			.mission(mission)
			.missionQuest(quest)
			.user(user)
			.completedStatus(IN_PROGRESS).build();

		user.addUserMission(userMission);
		return userMission;
	}

	public static OpponentGoalStepsResult getOpponentGoalStepsResult(User opponentUser) {
		if (!opponentUser.getUserMissions().isEmpty()) {
			UserMission opponentCurrentUserMission = opponentUser.getCurrentUserMission();
			int opponentGoalStep = (opponentCurrentUserMission != null && validateTodayDateMission(opponentCurrentUserMission)) ? opponentCurrentUserMission.getMission().getStepCount() : 0;
			assert opponentCurrentUserMission != null;
			return OpponentGoalStepsResult.of(opponentGoalStep);
		}
		throw new MissionException(NOT_EXIST_TODAY_MISSION_CHOICE);
	}


	// 오늘의 미션 리스트 비어있는지 체크
	public static void checkedUserMissionEmpty(User user) {
		if (user.getUserMissions().isEmpty()) {
			throw new MissionException(EMPTY_USER_MISSIONS);
		}
	}

	// 매칭된 유저의 탈퇴 여부 검사
	public static void checkMatchedUserWithdraw(User opponentUser) {
		if (opponentUser.isDeleted()) {
			throw new UserException(ALREADY_WITHDRAW_OPPONENT_USER);
		}
	}

	// 오늘의 미션 선정 여부 검사
	public void checkMissionChoice(UserMission todayMission) {
		if (!validateTodayDateMission(todayMission)) {
			throw new MissionException(NOT_CHOICE_TODAY_MISSION);
		}
	}

	// 오늘의 미션에 대한 유효성 검사
	public static boolean validateTodayDateMission(UserMission todayMission) {
		if (!todayMission.isNowDate() || todayMission.isEmptyUserMission()) {
			log.info("유효하지 않은 오늘의 미션! (아직 유저가 선택 X)");
			return false;
		}
		return true;
	}

	private Mission getRandomSingleMission(List<Mission> missions) {
		int index = (int) (Math.random() * missions.size());
		return missions.get(index);
	}

	/*public void createHistoryDummy(List<User> parentchildUsers) {
		if (parentchildUsers.size() == 2) {
			createUserMissionHistoryDummy(parentchildUsers.get(0), parentchildUsers.get(1));
		}
	}*/

	/*private List<UserMission> createUserMissionHistoryDummy(User user, User matchedUser, MissionQuest quest) {
		return List.of(
			createUserMission(user, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/um2.jpg", SUCCESS, LocalDateTime.of(2024, 1, 18, 12, 0, 0));
			createUserMission(user, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/cat.jpg", SUCCESS, LocalDateTime.of(2024, 1, 17, 12, 0, 0));
			createUserMission(user, null, FAIL, LocalDateTime.of(2024, 1, 16, 12, 0, 0));
			createUserMission(user, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/tl_baseball.jpg", SUCCESS, LocalDateTime.of(2024, 1, 15, 12, 0, 0));
			createUserMission(user, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/motivoo.jpg", SUCCESS, LocalDateTime.of(2024, 1, 13, 12, 0, 0));
			createUserMission(user, null, FAIL, LocalDateTime.of(2024, 1, 11, 12, 0, 0));
			createUserMission(user, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/jo.jpg", SUCCESS, LocalDateTime.of(2024, 1, 9, 12, 0, 0));
			createUserMission(matchedUser, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/motivoo_all1.png", SUCCESS, LocalDateTime.of(2024, 1, 18, 12, 0, 0));
			createUserMission(matchedUser, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/leejs.jpg", SUCCESS, LocalDateTime.of(2024, 1, 17, 12, 0, 0));
			createUserMission(matchedUser, null, FAIL, LocalDateTime.of(2024, 1, 16, 12, 0, 0));
			createUserMission(matchedUser, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/motivoo_all3.png", SUCCESS, LocalDateTime.of(2024, 1, 15, 12, 0, 0));
			createUserMission(matchedUser, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/motivoo_all2.jpg", SUCCESS, LocalDateTime.of(2024, 1, 13, 12, 0, 0));
			createUserMission(matchedUser, null, FAIL, LocalDateTime.of(2024, 1, 11, 12, 0, 0));
			createUserMission(matchedUser, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/um.jpg", SUCCESS, LocalDateTime.of(2024, 1, 9, 12, 0, 0)));
	}*/

	/*public UserMission createUserMission(User user, String imgUrl, CompletedStatus completedStatus, LocalDateTime createdAt, Mission mission, MissionQuest quest) {
		UserMission userMission = UserMission.builderForDemo()
			.completedStatus(completedStatus)
			.mission(mission)
			.missionQuest(quest)
			.imgUrl(imgUrl)
			.user(user).build();

		user.addUserMission(userMission);
		return userMission;

		userMission.updateCreatedAt(createdAt);
		userMission.updateUpdatedAt(createdAt);

		userMissionRetriever.saveUserMission(userMission);
	}*/

}
