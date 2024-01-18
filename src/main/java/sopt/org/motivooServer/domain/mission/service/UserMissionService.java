package sopt.org.motivooServer.domain.mission.service;

import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.*;
import static sopt.org.motivooServer.domain.mission.entity.CompletedStatus.*;
import static sopt.org.motivooServer.domain.mission.exception.MissionExceptionType.*;
import static sopt.org.motivooServer.domain.parentchild.exception.ParentchildExceptionType.*;
import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;
import static sopt.org.motivooServer.global.external.s3.S3BucketDirectory.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.health.entity.ExerciseLevel;
import sopt.org.motivooServer.domain.health.entity.Health;
import sopt.org.motivooServer.domain.health.entity.HealthNote;
import sopt.org.motivooServer.domain.health.exception.HealthException;
import sopt.org.motivooServer.domain.health.repository.HealthRepository;
import sopt.org.motivooServer.domain.mission.dto.request.MissionImgUrlRequest;
import sopt.org.motivooServer.domain.mission.dto.request.MissionStepStatusRequest;
import sopt.org.motivooServer.domain.mission.dto.request.TodayMissionChoiceRequest;
import sopt.org.motivooServer.domain.mission.dto.response.MissionHistoryResponse;
import sopt.org.motivooServer.domain.mission.dto.response.MissionImgUrlResponse;
import sopt.org.motivooServer.domain.mission.dto.response.MissionStepStatusResponse;
import sopt.org.motivooServer.domain.mission.dto.response.TodayMissionResponse;
import sopt.org.motivooServer.domain.mission.entity.CompletedStatus;
import sopt.org.motivooServer.domain.mission.entity.Mission;
import sopt.org.motivooServer.domain.mission.entity.MissionQuest;
import sopt.org.motivooServer.domain.mission.entity.MissionType;
import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.mission.entity.UserMissionChoices;
import sopt.org.motivooServer.domain.mission.exception.MissionException;
import sopt.org.motivooServer.domain.mission.repository.MissionQuestRepository;
import sopt.org.motivooServer.domain.mission.repository.MissionRepository;
import sopt.org.motivooServer.domain.mission.repository.UserMissionChoicesRepository;
import sopt.org.motivooServer.domain.mission.repository.UserMissionRepository;
import sopt.org.motivooServer.domain.parentchild.exception.ParentchildException;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.entity.UserType;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.repository.UserRepository;
import sopt.org.motivooServer.global.external.firebase.FirebaseService;
import sopt.org.motivooServer.global.external.s3.PreSignedUrlResponse;
import sopt.org.motivooServer.global.external.s3.S3BucketDirectory;
import sopt.org.motivooServer.global.external.s3.S3Service;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserMissionService {
	private final UserMissionRepository userMissionRepository;
	private final UserMissionChoicesRepository userMissionChoicesRepository;
	private final UserRepository userRepository;
	private final MissionRepository missionRepository;
	private final MissionQuestRepository missionQuestRepository;
	private final HealthRepository healthRepository;
	private final S3Service s3Service;
	private final FirebaseService firebaseService;

	private static final int MAX_MISSION_CHOICES = 2;

	@Transactional
	public MissionImgUrlResponse getMissionImgUrl(final MissionImgUrlRequest request, final Long userId) {
		User user = getUserById(userId);
		checkedUserMissionEmpty(user);
		checkMatchedUserWithdraw(user);

		UserMission todayMission = user.getCurrentUserMission();
		checkMissionChoice(todayMission);
		checkMissionStepComplete(todayMission);

		PreSignedUrlResponse preSignedUrl = s3Service.getUploadPreSignedUrl(
			S3BucketDirectory.of(request.imgPrefix()));

		String imgUrl = s3Service.getURL(MISSION_PREFIX.value() + preSignedUrl.fileName());
		todayMission.updateImgUrl(s3Service.getImgByFileName(request.imgPrefix(), preSignedUrl.fileName()));
		todayMission.updateCompletedStatus(SUCCESS);
		return MissionImgUrlResponse.of(preSignedUrl.url(), preSignedUrl.fileName());
	}


	@Transactional
	public MissionHistoryResponse getUserMissionHistory(final Long userId) {
		User myUser = getUserById(userId);
		User opponentUser = getMatchedUserWith(myUser);
		if (myUser.getUserMissions().isEmpty()) {
			return MissionHistoryResponse.of(myUser);
		}

		UserMission todayMission = myUser.getCurrentUserMission();
		//checkMissionChoice(todayMission);

		Map<LocalDate, List<UserMission>> missionsByDate = groupUserMissionsByDate(userId, opponentUser.getId());
		log.info("missionsByDate size: {}", missionsByDate.size());
		for (LocalDate localDateTime : missionsByDate.keySet()) {
			log.info("missionsByDate.get(localDateTime) size: {}", missionsByDate.get(localDateTime).size());

			missionsByDate.get(localDateTime).stream()
				.filter(um -> um.getMission().getTarget().equals(UserType.NONE))
				.forEach(um -> um.updateCompletedStatus(NONE));
			// log.info("key={}, value={} 🥹{}", localDateTime, missionsByDate.get(localDateTime).get(0).getMission().getContent(),
			// 	missionsByDate.get(localDateTime).get(1).getMission().getContent());
		}
		return MissionHistoryResponse.of(myUser, todayMission, missionsByDate);
	}

	private Map<LocalDate, List<UserMission>> groupUserMissionsByDate(Long myUserId, Long opponentUserId) {
		List<User> users = userRepository.findAllByIds(Arrays.asList(myUserId, opponentUserId));  // 둘 중 1명이 탈퇴할 경우를 대비, ID값으로만 조회
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
		userMissionRepository.saveAll(emptyUserMissions);

		// 그룹화
		return users.stream()
			.flatMap(user -> user.getUserMissions().stream())
			.collect(Collectors.groupingBy(userMission -> userMission.getCreatedAt().toLocalDate()));
	}

	@Transactional
	public Long choiceTodayMission(final TodayMissionChoiceRequest request, final Long userId) {
		User user = getUserById(userId);
		checkMatchedUserWithdraw(user);

		validateTodayMissionRequest(request.missionId(), user);

		Mission mission = getMissionById(request.missionId());
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
	public MissionStepStatusResponse getMissionCompleted(final MissionStepStatusRequest request, final Long userId) {
		User myUser = getUserById(userId);
		User opponentUser = getMatchedUserWith(myUser);

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
			return MissionStepStatusResponse.of(myUser, opponentUser, myGoalStep, opponentGoalStep, false, false);
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
				return MissionStepStatusResponse.of(myUser, opponentUser, myGoalStep, opponentGoalStep, false, false);
			}
			myGoalStep = myCurrentUserMission.getMission().getStepCount();
			boolean stepCountCompleted = myStep >= myCurrentUserMission.getMission().getStepCount();

			return MissionStepStatusResponse.of(myUser, opponentUser, myGoalStep, opponentGoalStep, stepCountCompleted, myCurrentUserMission.getImgUrl() != null);
		}

		return MissionStepStatusResponse.of(myUser, opponentUser, myGoalStep, opponentGoalStep, false, false);


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

	private boolean isStepCountCompleted(int currentStepCount, UserMission todayMission) {
		boolean isStepCountCompleted = currentStepCount >= todayMission.getMission().getStepCount();
		if (isStepCountCompleted) {
			todayMission.updateCompletedStatus(SUCCESS);
			log.info("오늘의 미션 달성 완료로 DB 업데이트 반영!");
		}
		return isStepCountCompleted;
	}

	@Transactional  // TODO 여기 최대한 분리해보자
	public TodayMissionResponse getTodayMission(final Long userId) {
		User user = getUserById(userId);
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
			return TodayMissionResponse.of(todayMissionChoices);
		}

		if (!user.getUserMissionChoice().isEmpty()) {
			return TodayMissionResponse.of(user.getUserMissionChoice());
		}

		// 2) 필터링 로직을 한 번 이상 거친 경우 -> 저장된 거 가져오기
		UserMission todayMission = user.getCurrentUserMission();

		if (!validateTodayDateMission(todayMission) && user.getUserMissionChoice().isEmpty()) {
			log.info("유저 {}의 UserMissions 선택지 리스트가 비어 있음", user.getNickname());

			List<UserMissionChoices> todayMissionChoices = filterTodayUserMission(user);
			user.setPreUserMissionChoice(todayMissionChoices);
			log.info("유저 오늘의 미션 선택지 세팅 완료! : {}", todayMissionChoices.size());
			return TodayMissionResponse.of(todayMissionChoices);
		}

		if (!validateTodayDateMission(todayMission) && !user.getUserMissionChoice().isEmpty()) {
			log.info("오늘의 미션 선택지가 세팅된 상태: {}", user.getUserMissionChoice().size());
			return TodayMissionResponse.of(user.getUserMissionChoice());
		}

		// 상대 측에서 미션 히스토리를 먼저 조회한 경우
		if (user.getUserMissionChoice().isEmpty() && todayMission.getMission().getTarget().equals(UserType.NONE)) {
			List<UserMissionChoices> todayMissionChoices = filterTodayUserMission(user);
			user.setPreUserMissionChoice(todayMissionChoices);
			log.info("유저 오늘의 미션 선택지 세팅 완료! : {}", todayMissionChoices.size());
			return TodayMissionResponse.of(todayMissionChoices);
		}

		/**
		 * 	 오늘의 미션이 선정된 경우
		 */
		log.info("오늘의 미션이 선정된 상태: {}", todayMission.getMission().getContent());
		return TodayMissionResponse.of(todayMission);
		// throw new MissionException(FAIL_TO_GET_TODAY_MISSION);
	}


	private List<UserMissionChoices> filterTodayUserMission(User user) {
		final List<Mission> missionChoicesFiltered = new ArrayList<>();

		// 부모 미션 or 자식 미션 리스트
		List<Mission> missions = missionRepository.findMissionsByTarget(user.getType());
		log.info("{} 미션 리스트 가져옴", user.getType().getValue());
		Health health = getHealthByUser(user);
		log.info("Health: {}", health.getId());

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

		List<UserMissionChoices> missionChoices = new ArrayList<>();
		for (int i = 0; i < Math.min(missionChoicesFiltered.size(), 2); i++) {
			UserMissionChoices missionChoice = UserMissionChoices.builder()
				.mission(missionChoicesFiltered.get(i))
				.user(user)
				.build();
			missionChoices.add(userMissionChoicesRepository.save(missionChoice));
		}

		// user.setPreUserMissionChoice(missionChoices);
		// if (missionChoices.isEmpty() || missionChoices.size() == 1) {
		// 	throw new MissionException(NOT_FILTERED_TODAY_MISSION);
		// }
		return missionChoices;
	}

	private List<UserMissionChoices> filterTodayUserMissionV2(User user) {
		// 부모 미션 or 자식 미션 리스트
		List<Mission> missions = missionRepository.findMissionsByTarget(user.getType());
		Health health = getHealthByUser(user);
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
			.map(userMissionChoicesRepository::save)
			.toList();
	}

	@NotNull
	private UserMission createTodayUserMission(Mission mission, User user) {
		MissionQuest missionQuest = getRandomMissionQuest();

		UserMission userMission = UserMission.builder()
			.mission(mission)
			.missionQuest(missionQuest)
			.user(user)
			.completedStatus(IN_PROGRESS).build();

		userMissionRepository.save(userMission);
		user.addUserMission(userMission);
		return userMission;
	}

	private MissionQuest getRandomMissionQuest() {
		MissionQuest missionQuest = missionQuestRepository.findRandomMissionQuest();
		if (missionQuest == null) {
			throw new MissionException(MISSION_QUEST_NOT_FOUND);
		}
		return missionQuest;
	}

	@NotNull
	private UserMission createEmptyUserMission(User user, LocalDate date) {
		UserMission um = UserMission.builderForEmpty()
			.completedStatus(NONE)
			.user(user)
			.mission(getEmptyMission())
			.missionQuest(getRandomMissionQuest())
			.build();

		user.getUserMissions().add(um);
		um.updateCreatedAt(date.atStartOfDay());  // 동일한 날짜로 세팅
		return um;
	}

	// 매칭된 유저의 탈퇴 여부 검사
	private void checkMatchedUserWithdraw(User user) {
		User opponentUser = getMatchedUserWith(user);
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

	private User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			() -> new UserException(USER_NOT_FOUND));
	}

	private Mission getMissionById(Long missionId) {
		return missionRepository.findById(missionId).orElseThrow(
			() -> new MissionException(MISSION_NOT_FOUND));
	}

	private UserMission getUserMission(Long userMissionId) {
		return userMissionRepository.findById(userMissionId).orElseThrow(
			() -> new MissionException(USER_MISSION_NOT_FOUND));
	}

	// 자신과 매칭된 부모자녀 유저 조회
	private User getMatchedUserWith(User user) {
		return userRepository.findByIdAndParentchild(user.getId(), user.getParentchild()).orElseThrow(
			() -> new ParentchildException(NOT_EXIST_PARENTCHILD_USER));
	}

	// 유저의 건강정보 조회 (주의사항 반영 의도)
	private Health getHealthByUser(User user) {
		return healthRepository.findByUser(user).orElseThrow(
			() -> new HealthException(HEALTH_NOT_FOUND));
	}

	// 미션 히스토리 - 상대방의 오늘의 미션 미선정 시
	private Mission getEmptyMission() {
		return missionRepository.findMissionsByTarget(UserType.NONE).get(0);
	}


}
