package sopt.org.motivoo.domain.mission.service;

import static sopt.org.motivoo.domain.external.s3.S3BucketDirectory.*;
import static sopt.org.motivoo.domain.mission.entity.CompletedStatus.*;
import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;
import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.common.advice.BusinessException;
import sopt.org.motivoo.domain.external.firebase.FirebaseService;
import sopt.org.motivoo.domain.external.s3.PreSignedUrlResponse;
import sopt.org.motivoo.domain.external.s3.S3BucketDirectory;
import sopt.org.motivoo.domain.external.s3.S3Service;
import sopt.org.motivoo.domain.health.entity.ExerciseLevel;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.entity.HealthNote;
import sopt.org.motivoo.domain.health.repository.HealthRetriever;
import sopt.org.motivoo.domain.mission.dto.request.MissionImgUrlCommand;
import sopt.org.motivoo.domain.mission.dto.request.MissionStepStatusCommand;
import sopt.org.motivoo.domain.mission.dto.request.TodayMissionChoiceCommand;
import sopt.org.motivoo.domain.mission.dto.response.MissionHistoryResult;
import sopt.org.motivoo.domain.mission.dto.response.MissionImgUrlResult;
import sopt.org.motivoo.domain.mission.dto.response.MissionStepStatusResult;
import sopt.org.motivoo.domain.mission.dto.response.OpponentGoalStepsResult;
import sopt.org.motivoo.domain.mission.dto.response.TodayMissionResult;
import sopt.org.motivoo.domain.mission.entity.CompletedStatus;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.mission.entity.MissionQuest;
import sopt.org.motivoo.domain.mission.entity.MissionType;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;
import sopt.org.motivoo.domain.mission.exception.MissionException;
import sopt.org.motivoo.domain.mission.repository.MissionQuestRetriever;
import sopt.org.motivoo.domain.mission.repository.MissionRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionChoicesRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionRetriever;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.entity.UserType;
import sopt.org.motivoo.domain.user.exception.UserException;
import sopt.org.motivoo.domain.user.repository.UserRepository;
import sopt.org.motivoo.domain.user.repository.UserRetriever;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserMissionService {
	private final UserMissionRetriever userMissionRetriever;
	private final UserMissionChoicesRetriever userMissionChoicesRetriever;
	private final UserRetriever userRetriever;
	private final MissionRetriever missionRetriever;
	private final MissionQuestRetriever missionQuestRetriever;
	private final HealthRetriever healthRetriever;

	private final S3Service s3Service;
	private final FirebaseService firebaseService;

	private static final int MAX_MISSION_CHOICES = 2;

	@Transactional
	public MissionImgUrlResult getMissionImgUrl(final MissionImgUrlCommand request, final Long userId) {
		User user = userRetriever.getUserById(userId);
		checkedUserMissionEmpty(user);
		checkMatchedUserWithdraw(user);

		UserMission todayMission = user.getCurrentUserMission();
		checkMissionChoice(todayMission);
		// checkMissionStepComplete(todayMission);

		PreSignedUrlResponse preSignedUrl = s3Service.getUploadPreSignedUrl(
			S3BucketDirectory.of(request.imgPrefix()));


		todayMission.updateImgUrl(s3Service.getImgByFileName(request.imgPrefix(), preSignedUrl.fileName()));
		todayMission.updateCompletedStatus(SUCCESS);
		return MissionImgUrlResult.of(preSignedUrl.url(), preSignedUrl.fileName());
	}


	@Transactional
	public MissionHistoryResult getUserMissionHistory(final Long userId) {
		User myUser = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(myUser);
		if (myUser.getUserMissions().isEmpty()) {
			return MissionHistoryResult.of(myUser);
		}

		UserMission todayMission = myUser.getCurrentUserMission();
		//checkMissionChoice(todayMission);

		Map<LocalDate, List<UserMission>> missionsByDate = groupUserMissionsByDate(userId, opponentUser.getId());
		log.info("missionsByDate size: {}", missionsByDate.size());
		for (LocalDate localDateTime : missionsByDate.keySet()) {
			log.info("missionsByDate.get(localDateTime) size: {}-{}", missionsByDate.get(localDateTime).size(), localDateTime);

			missionsByDate.get(localDateTime).stream()
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
				});

			if (!localDateTime.equals(LocalDate.now())) {
				missionsByDate.get(localDateTime).stream()
					.filter(um -> um.getImgUrl() == null && !um.getMission().getTarget().equals(UserType.NONE))
					.forEach(um -> um.updateCompletedStatus(FAIL));
			}
			missionsByDate.get(localDateTime).stream()
				.filter(um -> um.getImgUrl() != null && !um.getMission().getTarget().equals(UserType.NONE))
				.forEach(um -> um.updateCompletedStatus(SUCCESS));

			missionsByDate.get(localDateTime).stream()
				.filter(um -> um.getMission().getTarget().equals(UserType.NONE))
				.forEach(um -> um.updateCompletedStatus(NONE));

			// log.info("key={}, value={} 🥹{}", localDateTime, missionsByDate.get(localDateTime).get(0).getMission().getContent(),
			// 	missionsByDate.get(localDateTime).get(1).getMission().getContent());
		}
		return MissionHistoryResult.of(myUser, todayMission, missionsByDate);
	}

	private Map<LocalDate, List<UserMission>> groupUserMissionsByDate(Long myUserId, Long opponentUserId) {
		List<User> users = userRetriever.getUsersByIds(myUserId, opponentUserId);  // 둘 중 1명이 탈퇴할 경우를 대비, ID값으로만 조회
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

			// userDates 과 myDates를 비교하여 없는 날짜에는 빈 값의 UserMission 생성해주기
			userDates.forEach(date -> {
				if (!myDates.contains(date)) {
					UserMission um = createEmptyUserMission(user, date);
					emptyUserMissions.add(um);
				}
			});
		});
		userMissionRetriever.saveAll(emptyUserMissions);

		// 그룹화
		Map<LocalDate, List<UserMission>> dateGroups = users.stream()
			.flatMap(user -> user.getUserMissions().stream())
			.collect(Collectors.groupingBy(userMission -> userMission.getCreatedAt().toLocalDate()));

		Map<LocalDate, List<UserMission>> sortedGroups = new TreeMap<>(Comparator.reverseOrder());
		sortedGroups.putAll(dateGroups);

		return sortedGroups;
	}

	@Transactional
	public Long choiceTodayMission(final TodayMissionChoiceCommand request, final Long userId) {
		User user = userRetriever.getUserById(userId);
		checkMatchedUserWithdraw(user);

		validateTodayMissionRequest(request.missionId(), user);

		Mission mission = missionRetriever.getMissionById(request.missionId());
		if (!user.getUserMissions().isEmpty()) {
			UserMission currentMission = user.getCurrentUserMission();

			if (validateTodayDateMission(currentMission)) {
				currentMission.updateMissionFromEmpty(mission);
				return currentMission.getId();
			}
		}

		UserMission userMission = createTodayUserMission(mission, user);
		user.clearPreUserMissionChoice();  // 오늘의 미션을 선정했다면, 선택지 리스트는 비워주기
		return userMission.getId();
	}

	private static void validateTodayMissionRequest(Long missionId, User user) {
		boolean missionExists = user.getUserMissionChoice().stream()
			.anyMatch(userMissionChoices -> userMissionChoices.getMission().getId().equals(missionId));

		if (!missionExists) {
			throw new MissionException(NOT_EXIST_TODAY_MISSION_CHOICE);
		}
	}

	@Transactional
	public MissionStepStatusResult getMissionCompleted(final MissionStepStatusCommand request, final Long userId) {
		User myUser = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(myUser);

		int myStep = request.myStepCount();
		int opponentStep = request.opponentStepCount();

		/*try {
			Map<String, Integer> userNowStepCounts = firebaseService.selectUserStep(List.of(myUser.getId(), opponentUser.getId()));
			log.info("userNowStepCount Map - size: {}, 1번: {}", userNowStepCounts.size(), userNowStepCounts.get(userId.toString()));
			myStep = userNowStepCounts.get(myUser.getId().toString());
			opponentStep = userNowStepCounts.get(opponentUser.getId().toString());
		} catch (CannotCreateTransactionException | NullPointerException e) {
			log.error("트랜잭션 처리 실패! - 유저 미션 달성 상태 업데이트를 위한 FB 조회");
		}*/

		int myGoalStep = 0;
		int opponentGoalStep = 0;

		log.info("현재 접속한 유저 - {} X 나와 매칭된 부모자녀 유저 - {}", myUser.getNickname(), opponentUser.getNickname());

		boolean myUserMissionsEmpty = myUser.getUserMissions().isEmpty();
		boolean opponentUserMissionsEmpty = opponentUser.getUserMissions().isEmpty();

		if (myUserMissionsEmpty && opponentUserMissionsEmpty) {
			return MissionStepStatusResult.of(myUser, opponentUser, myGoalStep, opponentGoalStep, false, false);
		}

		if (!opponentUserMissionsEmpty) {
			UserMission opponentCurrentUserMission = opponentUser.getCurrentUserMission();
			opponentGoalStep = (opponentCurrentUserMission != null && validateTodayDateMission(opponentCurrentUserMission)) ? opponentCurrentUserMission.getMission().getStepCount() : 0;
			assert opponentCurrentUserMission != null;
			// isStepCountCompleted(opponentStep, opponentCurrentUserMission);
		}

		if (!myUserMissionsEmpty) {
			UserMission myCurrentUserMission = myUser.getCurrentUserMission();
			if (!validateTodayDateMission(myCurrentUserMission)) {
				return MissionStepStatusResult.of(myUser, opponentUser, myGoalStep, opponentGoalStep, false, false);
			}
			myGoalStep = myCurrentUserMission.getMission().getStepCount();
			boolean stepCountCompleted = myStep >= myCurrentUserMission.getMission().getStepCount();

			return MissionStepStatusResult.of(myUser, opponentUser, myGoalStep, opponentGoalStep, stepCountCompleted, myCurrentUserMission.getImgUrl() != null);
		}

		return MissionStepStatusResult.of(myUser, opponentUser, myGoalStep, opponentGoalStep, false, false);


	/*	UserMission todayMission = myUser.getCurrentUserMission();
		UserMission opponentTodayMission = opponentUser.getCurrentUserMission();
		if (opponentTodayMission != null) {
			opponentGoalStep = opponentTodayMission.getMission().getStepCount();
		}


		if (todayMission != null && !validateTodayDateMission(todayMission)) {
			log.info("todayMission = {} at {}", todayMission.getMission().getContent(), todayMission.getCreatedAt());
			checkMissionChoice(todayMission);
			myGoalStep = todayMission.getMission().getStepCount();

			int currentStepCount = request.myStepCount();

			return MissionStepStatusResponse.of(myUser, opponentUser, myGoalStep, opponentGoalStep, isStepCountCompleted(currentStepCount, todayMission));
		}
		return MissionStepStatusResponse.of(myUser, opponentUser, 0, 0, false);*/

	}

	public OpponentGoalStepsResult getOpponentGoalSteps(final Long userId) {
		User myUser = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(myUser);

		if (!opponentUser.getUserMissions().isEmpty()) {
			UserMission opponentCurrentUserMission = opponentUser.getCurrentUserMission();
			int opponentGoalStep = (opponentCurrentUserMission != null && validateTodayDateMission(opponentCurrentUserMission)) ? opponentCurrentUserMission.getMission().getStepCount() : 0;
			assert opponentCurrentUserMission != null;
			return OpponentGoalStepsResult.of(opponentGoalStep);
		}
		throw new MissionException(NOT_EXIST_TODAY_MISSION_CHOICE);
	}

	private boolean isStepCountCompleted(int currentStepCount, UserMission todayMission) {
		boolean isStepCountCompleted = currentStepCount >= todayMission.getMission().getStepCount();
		if (isStepCountCompleted) {
			todayMission.updateCompletedStatus(SUCCESS);
			log.info("오늘의 미션 달성 완료로 DB 업데이트 반영!");
		}
		return isStepCountCompleted;
	}

	@Transactional  // TODO 여기 최대한 분리해보자
	public TodayMissionResult getTodayMission(final Long userId) {
		User user = userRetriever.getUserById(userId);
		checkMatchedUserWithdraw(user);
		log.info("TodayMissionChoices이 있을까, 없을까? {}개 있음 ㅋㅋ", user.getUserMissionChoice().size());

		/**
		 * 아직 오늘의 미션이 선정되지 않은 경우
		 * */
		// 1) 처음 가입한 유저의 경우 or 필터링 로직을 거친 적이 없는 경우 -> 필터 거치기
		if ((user.getUserMissions().isEmpty() && user.getUserMissionChoice().isEmpty())) {
			log.info("유저 {}의 UserMissions, UserMissionChoices 리스트가 비어 있음", user.getNickname());

			List<UserMissionChoices> todayMissionChoices = filterTodayUserMission(user);
			user.setPreUserMissionChoice(todayMissionChoices);
			log.info("첫 가입 유저 오늘의 미션 선택지 세팅 완료! : {}", todayMissionChoices.size());
			return TodayMissionResult.of(todayMissionChoices);
		}

		if (!user.getUserMissions().isEmpty()) {
			UserMission todayMission = user.getCurrentUserMission();
			if (validateTodayDateMission(todayMission) && !todayMission.getMission().getTarget().equals(UserType.NONE)) {
				user.clearPreUserMissionChoice();
				return TodayMissionResult.of(todayMission);
			}
		}
		if (!user.getUserMissionChoice().isEmpty()) {
			return TodayMissionResult.of(user.getUserMissionChoice());
		}

		// 2) 필터링 로직을 한 번 이상 거친 경우 -> 저장된 거 가져오기
		UserMission todayMission = user.getCurrentUserMission();

		if (!validateTodayDateMission(todayMission) && user.getUserMissionChoice().isEmpty()) {
			log.info("유저 {}의 UserMissions 선택지 리스트가 비어 있음", user.getNickname());

			List<UserMissionChoices> todayMissionChoices = filterTodayUserMission(user);
			user.setPreUserMissionChoice(todayMissionChoices);
			log.info("유저 오늘의 미션 선택지 세팅 완료! : {}", todayMissionChoices.size());
			return TodayMissionResult.of(todayMissionChoices);
		}

		if (!validateTodayDateMission(todayMission) && !user.getUserMissionChoice().isEmpty()) {
			log.info("오늘의 미션 선택지가 세팅된 상태: {}", user.getUserMissionChoice().size());
			return TodayMissionResult.of(user.getUserMissionChoice());
		}

		// 상대 측에서 미션 히스토리를 먼저 조회한 경우
		if (user.getUserMissionChoice().isEmpty() && todayMission.getMission().getTarget().equals(UserType.NONE)) {
			List<UserMissionChoices> todayMissionChoices = filterTodayUserMission(user);
			user.setPreUserMissionChoice(todayMissionChoices);
			log.info("유저 오늘의 미션 선택지 세팅 완료! : {}", todayMissionChoices.size());
			return TodayMissionResult.of(todayMissionChoices);
		}

		/**
		 * 	 오늘의 미션이 선정된 경우
		 */
		log.info("오늘의 미션이 선정된 상태: {}", todayMission.getMission().getContent());
		return TodayMissionResult.of(todayMission);
		// throw new MissionException(FAIL_TO_GET_TODAY_MISSION);
	}


	private List<UserMissionChoices> filterTodayUserMission(User user) {
		final List<Mission> missionChoicesFiltered = getFilteredMissions(user);

		List<UserMissionChoices> missionChoices = new ArrayList<>();
		for (int i = 0; i < Math.min(missionChoicesFiltered.size(), 2); i++) {
			UserMissionChoices missionChoice = UserMissionChoices.builder()
				.mission(missionChoicesFiltered.get(i))
				.user(user).build();
			missionChoices.add(userMissionChoicesRetriever.save(missionChoice));
		}

		// user.setPreUserMissionChoice(missionChoices);
		// if (missionChoices.isEmpty() || missionChoices.size() == 1) {
		// 	throw new MissionException(NOT_FILTERED_TODAY_MISSION);
		// }
		return missionChoices;
	}



	private List<UserMissionChoices> filterTodayUserMissionV2(User user) {
		// 부모 미션 or 자식 미션 리스트
		List<Mission> missions = missionRetriever.getMissionsByTarget(user);
		Health health = healthRetriever.getHealthByUser(user);
		List<HealthNote> userNotes = health.getHealthNotes();
		ExerciseLevel exerciseLevel = health.getExerciseLevel();

		return missions.parallelStream()
			.filter(mission -> {
				List<HealthNote> missionNotes = HealthNote.of(mission.getHealthNotes());
				boolean hasUserNotes = missionNotes.stream().anyMatch(userNotes::contains);
				boolean hasExerciseLevel = MissionType.of(mission.getType()).containsLevel(exerciseLevel);
				return !hasUserNotes && !hasExerciseLevel;
			})
			.limit(MAX_MISSION_CHOICES)
			.map(mission -> UserMissionChoices.builder()
				.mission(mission)
				.user(user).build())
			.map(userMissionChoicesRetriever::save)
			.toList();
	}

	@NotNull
	public List<Mission> getFilteredMissions(User user) {
		final List<Mission> missionChoicesFiltered = new ArrayList<>();

		// 부모 미션 or 자식 미션 리스트
		List<Mission> missions = missionRetriever.getMissionsByTarget(user);
		log.info("{} 미션 리스트 가져옴", user.getType().getValue());
		Health health = healthRetriever.getHealthByUser(user);

		List<HealthNote> userNotes = health.getHealthNotes();
		ExerciseLevel exerciseLevel = health.getExerciseLevel();

		log.info("Mission 필터링 시작!");
		for (Mission mission : missions) {
			List<HealthNote> missionNotes = HealthNote.of(mission.getHealthNotes());
			boolean hasUserNotes = missionNotes.stream().anyMatch(userNotes::contains);
			boolean hasExerciseLevel = MissionType.of(mission.getType()).containsLevel(exerciseLevel);

			if (!hasUserNotes && !hasExerciseLevel) {
				missionChoicesFiltered.add(mission);
			}
		}
		log.info("맞춤 Mission 리스트에 추가(Shuffle 전): {}가지", missionChoicesFiltered.size());
		Collections.shuffle(missionChoicesFiltered);

		return missionChoicesFiltered;
	}

	@NotNull
	private UserMission createTodayUserMission(Mission mission, User user) {
		MissionQuest missionQuest = missionQuestRetriever.getRandomMissionQuest();

		UserMission userMission = UserMission.builder()
			.mission(mission)
			.missionQuest(missionQuest)
			.user(user)
			.completedStatus(IN_PROGRESS).build();

		userMissionRetriever.saveUserMission(userMission);
		user.addUserMission(userMission);
		return userMission;
	}

	

	@NotNull
	private UserMission createEmptyUserMission(User user, LocalDate date) {
		UserMission um = UserMission.builderForEmpty()
			.completedStatus(NONE)
			.user(user)
			.mission(missionRetriever.getEmptyMission())
			.missionQuest(missionQuestRetriever.getRandomMissionQuest())
			.build();

		userMissionRetriever.saveUserMission(um);
		um.updateCreatedAt(date.atStartOfDay());  // 동일한 날짜로 세팅
		user.addUserMission(um);
		return um;
	}

	// 매칭된 유저의 탈퇴 여부 검사
	private void checkMatchedUserWithdraw(User user) {
		User opponentUser = userRetriever.getMatchedUserWith(user);
		if (opponentUser.isDeleted()) {
			throw new UserException(ALREADY_WITHDRAW_USER);
		}
	}

	// 오늘의 미션 리스트 비어있는지 체크
	private static void checkedUserMissionEmpty(User user) {
		if (user.getUserMissions().isEmpty()) {
			throw new MissionException(EMPTY_USER_MISSIONS);
		}
	}

	// 오늘의 미션 선정 여부 검사
	private void checkMissionChoice(UserMission todayMission) {
		if (!validateTodayDateMission(todayMission)) {
			throw new MissionException(NOT_CHOICE_TODAY_MISSION);
		}
	}

	// 오늘의 미션 걸음 수 달성 상태 확인
	private void checkMissionStepComplete(UserMission todayMission) {
		if (!todayMission.getCompletedStatus().equals(SUCCESS)) {
			throw new MissionException(NOT_COMPLETE_MISSION_STEPS_SUCCESS);
		}
	}

	// 오늘의 미션에 대한 유효성 검사
	private boolean validateTodayDateMission(UserMission todayMission) {
		if (!todayMission.getCreatedAt().toLocalDate().equals(LocalDate.now())) {
			log.info("오늘 날짜와 동일하지 않은 최근 미션!");
			return false;
		}
		return true;
	}


	// 데모데이용 더미 미션 히스토리 생성
	@Transactional
	public void demoHistory(final Long parentchildId) {
		List<User> parentchildUsers = userRetriever.getUsersByParentchildId(parentchildId);
		if (parentchildUsers.size() == 2) {
			createUserMissionHistoryDummy(parentchildUsers.get(0), parentchildUsers.get(1));
		}
	}

	private Mission getRandomSingleMission(List<Mission> missions) {
		int index = (int) (Math.random() * missions.size());
		return missions.get(index);
	}

	private void createUserMission(User user, String imgUrl, CompletedStatus completedStatus, LocalDateTime createdAt) {
		UserMission userMission = UserMission.builderForDemo()
			.completedStatus(completedStatus)
			.mission(getRandomSingleMission(getFilteredMissions(user)))
			.missionQuest(missionQuestRetriever.getRandomMissionQuest())
			.user(user).build();

		user.addUserMission(userMission);
		userMissionRetriever.saveUserMission(userMission);

		userMission.updateImgUrl(imgUrl);
		userMission.updateCreatedAt(createdAt);
		userMission.updateUpdatedAt(createdAt);

		userMissionRetriever.saveUserMission(userMission);
	}



	private void createUserMissionHistoryDummy(User user, User matchedUser) {
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
		createUserMission(matchedUser, "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/um.jpg", SUCCESS, LocalDateTime.of(2024, 1, 9, 12, 0, 0));
	}
}