package sopt.org.motivoo.domain.mission.service;

import static sopt.org.motivoo.domain.mission.entity.CompletedStatus.*;
import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;
import static sopt.org.motivoo.domain.mission.service.UserMissionManager.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.external.firebase.FirebaseService;
import sopt.org.motivoo.domain.external.s3.PreSignedUrlResponse;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.repository.HealthRetriever;
import sopt.org.motivoo.domain.mission.dto.request.GoalStepCommand;
import sopt.org.motivoo.domain.mission.dto.request.MissionImgUrlCommand;
import sopt.org.motivoo.domain.mission.dto.request.MissionStepStatusCommand;
import sopt.org.motivoo.domain.mission.dto.request.TodayMissionChoiceCommand;
import sopt.org.motivoo.domain.mission.dto.response.GoalStepResult;
import sopt.org.motivoo.domain.mission.dto.response.MissionHistoryResult;
import sopt.org.motivoo.domain.mission.dto.response.MissionImgUrlResult;
import sopt.org.motivoo.domain.mission.dto.response.MissionStepStatusResult;
import sopt.org.motivoo.domain.mission.dto.response.OpponentGoalStepsResult;
import sopt.org.motivoo.domain.mission.dto.response.TodayMissionResult;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.mission.entity.MissionQuest;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;
import sopt.org.motivoo.domain.mission.exception.MissionException;
import sopt.org.motivoo.domain.mission.repository.MissionQuestRetriever;
import sopt.org.motivoo.domain.mission.repository.MissionRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionChoicesRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionRepository;
import sopt.org.motivoo.domain.mission.repository.UserMissionRetriever;
import sopt.org.motivoo.domain.user.entity.User;
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

	private final UserMissionManager userMissionManager;

	private final FirebaseService firebaseService;

	@Transactional
	public MissionImgUrlResult getMissionImgUrl(final MissionImgUrlCommand request, final Long userId) {
		User user = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(user);

		checkedUserMissionEmpty(user);
		checkMatchedUserWithdraw(opponentUser);

		UserMission todayMission = user.getCurrentUserMission();
		userMissionManager.checkMissionChoice(todayMission);
		// checkMissionStepComplete(todayMission);
		PreSignedUrlResponse preSignedUrl = userMissionManager.getMissionSuccessImgUrl(todayMission, request);

		return MissionImgUrlResult.of(preSignedUrl.url(), preSignedUrl.fileName());
	}

	@Transactional
	public MissionHistoryResult getUserMissionHistory(final Long userId) {
		User myUser = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(myUser);

		// 유저의 미션 목록이 없는 경우
		if (myUser.getUserMissions().isEmpty()) {
			return MissionHistoryResult.of(myUser);
		}

		Map<LocalDate, List<UserMission>> missionsByDate = groupUserMissionsByDate(userId, opponentUser.getId());
		missionsByDate = userMissionManager.getMissionDateGroup(missionsByDate);

		UserMission todayMission = myUser.getCurrentUserMission();

		// 오늘의 미션을 아직 선택하지 않은 경우
		if (!validateTodayDateMission(todayMission)) {
			return MissionHistoryResult.of(myUser, missionsByDate);
		}

		return MissionHistoryResult.of(myUser, todayMission, missionsByDate);
	}

	private Map<LocalDate, List<UserMission>> groupUserMissionsByDate(Long myUserId, Long opponentUserId) {
		List<User> parentChildUsers = userRetriever.getUsersByIds(myUserId, opponentUserId);  // 둘 중 1명이 탈퇴할 경우를 대비, ID값으로만 조회
		// List<UserMission> emptyUserMissions = userMissionManager.createEmptyDateMissions(parentchildUsers);
		// userMissionRetriever.saveAll(emptyUserMissions);

		log.info("성립된 부모자식 관계: {}-{} X {}-{}, 관계",
			parentChildUsers.get(0).getNickname(), parentChildUsers.get(0).getType(),
			parentChildUsers.get(1).getNickname(), parentChildUsers.get(1).getType());

		// 그룹화
		return userMissionManager.sortMissionsByDate(parentChildUsers);
	}

	@Transactional
	public Long choiceTodayMission(final TodayMissionChoiceCommand request, final Long userId) {
		User user = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(user);

		checkMatchedUserWithdraw(opponentUser);

		validateTodayMissionRequest(request.missionId(), user);

		Mission mission = missionRetriever.getMissionById(request.missionId());
		if (!user.getUserMissions().isEmpty()) {
			UserMission currentMission = user.getCurrentUserMission();

			if (validateTodayDateMission(currentMission)) {
				currentMission.updateMissionFromEmpty(mission);
				return currentMission.getId();
			}
		}

		MissionQuest missionQuest = missionQuestRetriever.getRandomMissionQuest();
		// UserMission userMission = userMissionManager.createTodayUserMission(mission, user, missionQuest);
		userMissionRetriever.updateUserMission(user, mission, missionQuest);
		// userMissionRetriever.saveUserMission(userMission);
		UserMission todayMission = user.getCurrentUserMission();

		// user.clearPreUserMissionChoice();  // 오늘의 미션을 선정했다면, 선택지 리스트는 비워주기
		return todayMission.getId();
	}

	private void validateTodayMissionRequest(Long missionId, User user) {
		boolean missionExists = userMissionChoicesRetriever.getUserMissionChoice(user).stream()
			.anyMatch(choices -> choices.getMission().getId().equals(missionId));

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

		/*
		// TODO 파이어베이스 DB에 접근하도록 수정
		try {
			Map<String, Integer> userNowStepCounts = firebaseService.selectUserStep(List.of(myUser.getId(), opponentUser.getId()));
			log.info("userNowStepCount Map - size: {}, 1번: {}", userNowStepCounts.size(), userNowStepCounts.get(userId.toString()));
			myStep = userNowStepCounts.get(myUser.getId().toString());
			opponentStep = userNowStepCounts.get(opponentUser.getId().toString());
		} catch (CannotCreateTransactionException | NullPointerException e) {
			log.error("트랜잭션 처리 실패! - 유저 미션 달성 상태 업데이트를 위한 FB 조회");
		}*/

		return userMissionManager.updateStepStatusResult(myUser, opponentUser, myStep);
	}

	public OpponentGoalStepsResult getOpponentGoalSteps(final Long userId) {
		User myUser = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(myUser);

		return getOpponentGoalStepsResult(opponentUser);
	}

	private boolean isStepCountCompleted(int currentStepCount, UserMission todayMission) {
		boolean isStepCountCompleted = currentStepCount >= todayMission.getMission().getStepCount();
		if (isStepCountCompleted) {
			todayMission.updateCompletedStatus(SUCCESS);
			log.info("오늘의 미션 달성 완료로 DB 업데이트 반영!");
		}
		return isStepCountCompleted;
	}

	@Transactional
	public TodayMissionResult getTodayMission(final Long userId) {
		User user = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(user);

		checkMatchedUserWithdraw(opponentUser);

		/**
		 * 아직 오늘의 미션이 선정되지 않은 경우
		 */
		boolean existsUserMission = userMissionRetriever.existsByUser(user);
		boolean isFiltered = userMissionChoicesRetriever.existsByUser(user);

		// 1) 처음 가입한 유저의 경우 -> 미션 선택지 세팅 완료
		if (!existsUserMission) {
			createEmptyMission(List.of(user, opponentUser));
			return getMissionChoicesResult(user);
		}

		UserMission todayMission = user.getCurrentUserMission();

		// 2) 필터링 로직을 한 번 이상 거친 경우 -> 저장된 미션 선택지 가져오기
		if (isFiltered && todayMission.isEmptyUserMission()) {
			List<UserMissionChoices> missionChoice = userMissionChoicesRetriever.getUserMissionChoice(user);
			return TodayMissionResult.of(missionChoice);
		}

		log.info("TodayMissionChoices이 있을까, 없을까? {}개 있음 ㅋㅋ", userMissionChoicesRetriever.getUserMissionChoice(user).size());
		log.info("TodayMission의 상태: {} 타깃", todayMission.getCreatedAt() + " " + todayMission.getMission().getTarget());

		// 3) 일반적인 경우 - 마션 선택지 필터링
		if (todayMission.isNowDate() && todayMission.isEmptyUserMission()) {
			log.info("필터링 GO");
			return getMissionChoicesResult(user);
		}
		// 3-1) TODO DB 초기화 하고 삭제해도 됨!
		if (!validateTodayDateMission(todayMission)) {
			createEmptyMission(List.of(user));
			return getMissionChoicesResult(user);
		}

		/**
		 * 	오늘의 미션이 선정된 경우
		 */
		log.info("오늘의 미션이 선정된 상태: {}", todayMission.getMission().getContent());
		return TodayMissionResult.of(todayMission);
		// throw new MissionException(FAIL_TO_GET_TODAY_MISSION);
	}

	private TodayMissionResult getMissionChoicesResult(User user) {
		log.info("유저 {}의 UserMissions, UserMissionChoices 리스트가 비어 있음", user.getNickname());

		List<UserMissionChoices> todayMissionChoices = filterTodayUserMission(user);
		log.info("첫 가입 유저 오늘의 미션 선택지 세팅 완료! : {}", todayMissionChoices.size());
		return TodayMissionResult.of(todayMissionChoices);
	}

	private List<UserMissionChoices> filterTodayUserMission(User user) {

		// 부모 미션 or 자식 미션 리스트
		List<Mission> missions = missionRetriever.getMissionsByTarget(user);
		log.info("{} 미션 리스트 가져옴", user.getType().getValue());
		Health health = healthRetriever.getHealthByUser(user);

		final List<Mission> filteredMissions = userMissionManager.getFilteredMissions(missions, health);
		List<UserMissionChoices> filteredMissionChoices = userMissionManager.getFilteredMissionChoices(user, filteredMissions);
		userMissionChoicesRetriever.saveAll(filteredMissionChoices);
		return filteredMissionChoices;
	}

	public void createEmptyMission(List<User> users) {
		Mission emptyMission = missionRetriever.getEmptyMission();
		MissionQuest missionQuest = missionQuestRetriever.getRandomMissionQuest();

		userMissionRetriever.bulkSaveInitUserMission(users, LocalDate.now(), emptyMission, missionQuest);

	}

	@Transactional
	public GoalStepResult updateGoalStepCount(final GoalStepCommand request, Long userId) {
		User user = userRetriever.getUserById(userId);
		UserMission todayMission = user.getCurrentUserMission();

		if (validateTodayDateMission(todayMission)) {
			int stepCount = todayMission.getMission().getStepCount();
			missionRetriever.updateStepCount(request.goalStepCount(), todayMission.getMission());
			return GoalStepResult.of(stepCount, request.goalStepCount());
		}
		throw new MissionException(FAIL_TO_UPDATE_GOAL_STEP_COUNT);
	}

	// 오늘의 미션 걸음 수 달성 상태 확인
	private void checkMissionStepComplete(UserMission todayMission) {
		if (!todayMission.getCompletedStatus().equals(SUCCESS)) {
			throw new MissionException(NOT_COMPLETE_MISSION_STEPS_SUCCESS);
		}
	}

	// 데모데이용 더미 미션 히스토리 생성
	/*@Transactional
	public void demoHistory(final Long parentchildId) {
		List<User> parentchildUsers = userRetriever.getUsersByParentchildId(parentchildId);
		if (parentchildUsers.size() == 2) {
			UserMission userMission = userMissionManager.createUserMission(parentchildUsers.get(0), parentchildUsers.get(1),
				filterTodayUserMission(parentchildUsers.get(0),
				missionQuestRetriever.getRandomMissionQuest());
			userMissionRetriever.saveUserMission(userMission);
		}
	}*/

}