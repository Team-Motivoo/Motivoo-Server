package sopt.org.motivooServer.domain.mission.service;

import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.*;
import static sopt.org.motivooServer.domain.mission.entity.CompletedStatus.*;
import static sopt.org.motivooServer.domain.mission.exception.MissionExceptionType.*;
import static sopt.org.motivooServer.domain.parentchild.exception.ParentchildExceptionType.*;
import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
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
import sopt.org.motivooServer.domain.mission.entity.Mission;
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
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.repository.UserRepository;
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

	private static final int MAX_MISSION_CHOICES = 2;

	// TODO userId를 이용하여 미션 리스트에서 하나 뽑아오기
	@Transactional
	public MissionImgUrlResponse getMissionImgUrl(final MissionImgUrlRequest request, final Long userId) {
		User user = getUserById(userId);
		UserMission todayMission = user.getCurrentUserMission();
		checkMissionChoice(todayMission);

		PreSignedUrlResponse preSignedUrl = s3Service.getUploadPreSignedUrl(
			S3BucketDirectory.of(request.imgPrefix()));
		todayMission.updateImgUrl(s3Service.getImgByFileName(request.imgPrefix(), preSignedUrl.fileName()));
		return MissionImgUrlResponse.of(preSignedUrl.url(), preSignedUrl.fileName());
	}

	public MissionHistoryResponse getUserMissionHistory(final Long userId) {
		User myUser = getUserById(userId);
		User opponentUser = getMatchedUserWith(myUser);

		UserMission todayMission = myUser.getCurrentUserMission();
		checkMissionChoice(todayMission);

		return MissionHistoryResponse.of(myUser, todayMission,
			userMissionRepository.findUserMissionsByUserOrderByCreatedAt(myUser),
			userMissionRepository.findUserMissionsByUserOrderByCreatedAt(opponentUser));
	}


	@Transactional
	public Long choiceTodayMission(final TodayMissionChoiceRequest request, final Long userId) {
		User user = getUserById(userId);
		validateTodayMissionRequest(request.missionId(), user);

		Mission mission = getMissionById(request.missionId());

		UserMission userMission = UserMission.builder()
			.mission(mission)
			.missionQuest(missionQuestRepository.findRandomMissionQuest())
			.user(user)
			.completedStatus(IN_PROGRESS).build();

		userMissionRepository.save(userMission);
		user.addUserMission(userMission);
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

		log.info("현재 접속한 유저 - {} X 나와 매칭된 부모자녀 유저 - {}", myUser.getNickname(), opponentUser.getNickname());

		UserMission todayMission = myUser.getCurrentUserMission();
		checkMissionChoice(todayMission);

		int currentStepCount = request.myStepCount();

		return MissionStepStatusResponse.of(myUser, opponentUser, isStepCountCompleted(currentStepCount, todayMission));
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
		log.info("TodayMission이 있을까, 없을까? {}개 있음 ㅋㅋ", user.getUserMissionChoice().size());

		/**
		 * UserMissionChoice 리스트 == Empty ?
		 * - 오늘의 미션을 선정한 경우, 비워주기
		 * - 아직 오늘의 미션이 선정되지 않은 경우
		 * - 첫 오늘의 미션을 부여받을 때
		 */

		// 처음 가입한 유저의 경우
		if (user.getUserMissions().isEmpty() && user.getUserMissionChoice().isEmpty()) {
			List<UserMissionChoices> todayMissionChoices = filterTodayUserMission(user);
			user.setPreUserMissionChoice(todayMissionChoices);
			log.info("첫 가입 유저 오늘의 미션 세팅 완료! : {}", todayMissionChoices.size());
			return TodayMissionResponse.of(todayMissionChoices);
		}

		// 오늘의 미션이 선정된 경우
		UserMission todayMission = user.getCurrentUserMission();
		if (todayMission == null || user.getUserMissions().isEmpty() || !validateTodayDateMission(todayMission)) {

			// 아직 오늘의 미션이 선정되지 않은 경우
			// 1) 필터링 로직을 거친 적이 없는 경우 -> 필터 거치기
			// 2) 필터링 로직을 한 번 이상 거친 경우 -> 저장된 거 가져오기
			if (user.getUserMissionChoice().isEmpty()) {
				log.info("유저 {}의 UserMissions 선택지 리스트가 비어 있음", user.getNickname());

				List<UserMissionChoices> todayMissionChoices = filterTodayUserMission(user);
				user.setPreUserMissionChoice(todayMissionChoices);
				log.info("오늘의 미션 세팅 완료! : {}", todayMissionChoices.size());
				return TodayMissionResponse.of(todayMissionChoices);
			} else {
				log.info("오늘의 미션이 세팅된 상태: {}", user.getUserMissionChoice().size());
				return TodayMissionResponse.of(user.getUserMissionChoice());
			}

		} else {
			log.info("오늘의 미션이 선정된 상태: {}", todayMission.getMission().getContent());
			return TodayMissionResponse.of(todayMission);
		}
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

		if (missionChoices.isEmpty() || missionChoices.size() == 1) {
			throw new MissionException(NOT_FILTERED_TODAY_MISSION);
		}
		user.setPreUserMissionChoice(missionChoices);
		log.info("user.getUserMissionChoice().size()={}", user.getUserMissionChoice().size());
		log.info("랜덤 선정된 오늘의 미션 선택지 - 1. {} 2. {}", user.getUserMissionChoice().get(0).getMission().getId(), user.getUserMissionChoice().get(1).getMission().getId());
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

	// 오늘의 미션 선정 여부 검사
	private void checkMissionChoice(UserMission todayMission) {
		if (!validateTodayDateMission(todayMission)) {
			throw new MissionException(NOT_CHOICE_TODAY_MISSION);
		}
	}

	// 오늘의 미션에 대한 유효성 검사
	private boolean validateTodayDateMission(UserMission todayMission) {
		if (!todayMission.getCreatedAt().toLocalDate().equals(LocalDate.now())) {
			log.info("오늘 날짜와 동일하지 않은 최근 미션!");
			return false;
			// throw new MissionException(NOT_CHOICE_TODAY_MISSION);
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
}
