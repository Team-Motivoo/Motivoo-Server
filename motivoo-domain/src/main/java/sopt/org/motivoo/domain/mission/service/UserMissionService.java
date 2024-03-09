package sopt.org.motivoo.domain.mission.service;

import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;
import static sopt.org.motivoo.domain.mission.service.UserMissionManager.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.repository.HealthRetriever;
import sopt.org.motivoo.domain.mission.dto.request.GoalStepCommand;
import sopt.org.motivoo.domain.mission.dto.request.StepStatusCommand;
import sopt.org.motivoo.domain.mission.dto.request.TodayMissionChoiceCommand;
import sopt.org.motivoo.domain.mission.dto.response.GoalStepResult;
import sopt.org.motivoo.domain.mission.dto.response.MissionHistoryResult;
import sopt.org.motivoo.domain.mission.dto.response.OpponentGoalStepsResult;
import sopt.org.motivoo.domain.mission.dto.response.StepStatusResult;
import sopt.org.motivoo.domain.mission.dto.response.TodayMissionResult;
import sopt.org.motivoo.domain.mission.entity.CompletedStatus;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.mission.entity.MissionQuest;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;
import sopt.org.motivoo.domain.mission.exception.MissionException;
import sopt.org.motivoo.domain.mission.repository.MissionQuestRetriever;
import sopt.org.motivoo.domain.mission.repository.MissionRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionChoicesRetriever;
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

	@Transactional
	public void updateMissionSuccess(final String imgUrl, final Long userId) {
		User user = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(user);

		checkedUserMissionEmpty(user);
		checkMatchedUserWithdraw(opponentUser);

		UserMission todayMission = user.getCurrentUserMission();
		// userMissionManager.checkMissionChoice(todayMission);
		// checkMissionStepComplete(todayMission);

		userMissionManager.updateMissionSuccess(todayMission, imgUrl);
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
		// userMissionRetriever.updateUserMission(user, mission, missionQuest);
		UserMission todayMission = user.getCurrentUserMission();
		setTodayMission(todayMission, mission, missionQuest);
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
	public StepStatusResult getMissionCompleted(final Long userId) {
		User myUser = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(myUser);

		return userMissionManager.updateStepStatusResult(myUser, opponentUser);
	}

	// 유저-목표 걸음수(오늘의 미션을 선정한 경우에 한하여)
	public Map<User, Integer> getUsersGoalStep() {
		Map<User, Integer> goalSteps = new HashMap<>();
		userRetriever.findAll().stream()
			.filter(u -> !u.getUserMissions().isEmpty() && validateTodayDateMission(u.getCurrentUserMission()) &&
				u.getCurrentUserMission().getCompletedStatus().equals(CompletedStatus.IN_PROGRESS))
			.forEach(u -> goalSteps.put(u, u.getCurrentUserMission().getMission().getStepCount()));

		return goalSteps;
	}

	public OpponentGoalStepsResult getOpponentGoalSteps(final Long userId) {
		User myUser = userRetriever.getUserById(userId);
		User opponentUser = userRetriever.getMatchedUserWith(myUser);

		return getOpponentGoalStepsResult(opponentUser);
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
		log.info("User {}의 UserMission이 존재하니? {} ", user.getNickname(), existsUserMission);
		log.info("오늘의 미션 선택지 필터링을 거쳤니? {} ", isFiltered);

		// 1) 처음 가입한 유저의 경우 -> 미션 선택지 세팅 완료
		if (!existsUserMission) {
			log.info("1. 첫 가입 유저 미션 필터링 진입");
			createEmptyMission(List.of(user, opponentUser));
			return getMissionChoicesResult(user);
		}

		UserMission todayMission = user.getCurrentUserMission();

		// 2) 필터링 로직을 한 번 이상 거친 경우 -> 저장된 미션 선택지 가져오기
		if (isFiltered && todayMission.isEmptyUserMission()) {
			log.info("2. 필터링 로직을 한 번 이상 거친 경우");
			List<UserMissionChoices> missionChoice = userMissionChoicesRetriever.getUserMissionChoice(user);
			return TodayMissionResult.of(missionChoice);
		}

		log.info("TodayMissionChoices이 있을까, 없을까? {}개 있음 ㅋㅋ", userMissionChoicesRetriever.getUserMissionChoice(user).size());
		log.info("TodayMission의 상태: {} 타깃", todayMission.getCreatedAt() + " " + todayMission.getMission().getTarget());

		// 3) 일반적인 경우 - 마션 선택지 필터링
		if (todayMission.isNowDate() && todayMission.isEmptyUserMission()) {
			log.info("3. 일반적인 경우(미션 선택지 필터링 이전)");
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
		user.addTodayUserMissionChoice(todayMissionChoices);
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

		List<User> filteredUsers = users.stream()
			.filter(user -> user.getUserMissions().isEmpty() || (!user.getUserMissions().isEmpty() && !user.getCurrentUserMission().isNowDate()))
			.toList();
		userMissionRetriever.bulkSaveInitUserMission(filteredUsers, LocalDate.now(), emptyMission, missionQuest);
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