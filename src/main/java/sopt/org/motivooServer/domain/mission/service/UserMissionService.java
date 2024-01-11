package sopt.org.motivooServer.domain.mission.service;

import static sopt.org.motivooServer.domain.mission.entity.CompletedStatus.*;
import static sopt.org.motivooServer.domain.mission.exception.MissionExceptionType.*;
import static sopt.org.motivooServer.domain.parentchild.exception.ParentchildExceptionType.*;
import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.mission.dto.request.MissionImgUrlRequest;
import sopt.org.motivooServer.domain.mission.dto.request.MissionStepStatusRequest;
import sopt.org.motivooServer.domain.mission.dto.request.TodayMissionChoiceRequest;
import sopt.org.motivooServer.domain.mission.dto.response.MissionHistoryResponse;
import sopt.org.motivooServer.domain.mission.dto.response.MissionImgUrlResponse;
import sopt.org.motivooServer.domain.mission.dto.response.MissionStepStatusResponse;
import sopt.org.motivooServer.domain.mission.dto.response.TodayMissionResponse;
import sopt.org.motivooServer.domain.mission.entity.Mission;
import sopt.org.motivooServer.domain.mission.entity.MissionQuestRepository;
import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.mission.exception.MissionException;
import sopt.org.motivooServer.domain.mission.repository.MissionRepository;
import sopt.org.motivooServer.domain.mission.repository.UserMissionRepository;
import sopt.org.motivooServer.domain.parentchild.exception.ParentchildException;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.repository.UserRepository;
import sopt.org.motivooServer.global.util.s3.PreSignedUrlResponse;
import sopt.org.motivooServer.global.util.s3.S3BucketDirectory;
import sopt.org.motivooServer.global.util.s3.S3Service;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserMissionService {

	private final UserMissionRepository userMissionRepository;
	private final UserRepository userRepository;
	private final MissionRepository missionRepository;
	private final MissionQuestRepository missionQuestRepository;
	private final S3Service s3Service;

	// TODO userId를 이용하여 미션 리스트에서 하나 뽑아오기
	@Transactional
	public MissionImgUrlResponse getMissionImgUrl(final MissionImgUrlRequest request, final Long missionId, final Long userId) {
		User user = getUserById(userId);
		UserMission userMission = getUserMission(missionId);

		PreSignedUrlResponse preSignedUrl = s3Service.getUploadPreSignedUrl(
			S3BucketDirectory.of(request.imgPrefix()));
		userMission.updateImgUrl(s3Service.getImgByFileName(request.imgPrefix(), preSignedUrl.fileName()));
		return MissionImgUrlResponse.of(preSignedUrl.url(), preSignedUrl.fileName());
	}

	public MissionHistoryResponse getUserMissionHistory(final Long userId) {
		User myUser = getUserById(userId);
		User opponentUser = getMatchedUserWith(myUser);

		UserMission todayMission = myUser.getCurrentUserMission();
		validateTodayDateMission(todayMission);

		return MissionHistoryResponse.of(myUser, todayMission,
			userMissionRepository.findUserMissionsByUserOrderByCreatedAt(myUser),
			userMissionRepository.findUserMissionsByUserOrderByCreatedAt(opponentUser));
	}


	@Transactional
	public Long choiceTodayMission(final TodayMissionChoiceRequest request, final Long userId) {
		User user = getUserById(userId);
		Mission mission = getMissionById(request.missionId());

		UserMission userMission = UserMission.builder()
			.mission(mission)
			.user(user)
			.completedStatus(IN_PROGRESS).build();
		userMissionRepository.save(userMission);
		return userMission.getId();
	}

	@Transactional
	public MissionStepStatusResponse getMissionCompleted(final MissionStepStatusRequest request, final Long userId) {
		User myUser = getUserById(userId);
		User opponentUser = getMatchedUserWith(myUser);

		log.info("현재 접속한 유저 - {} X 나와 매칭된 부모자녀 유저 - {}", myUser.getNickname(), opponentUser.getNickname());

		UserMission todayMission = myUser.getCurrentUserMission();
		validateTodayDateMission(todayMission);

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

		UserMission todayMission = user.getCurrentUserMission();
		if (validateTodayDateMission(todayMission)) {
			return TodayMissionResponse.of(todayMission, missionQuestRepository.findRandomMissionQuest());
		}

		List<UserMission> todayMissionChoices = filterTodayUserMission(user);
		user.setPreUserMissionChoice(todayMissionChoices);

		return TodayMissionResponse.of(todayMissionChoices);
	}


	// TODO 필터링 로직
	private List<UserMission> filterTodayUserMission(User user) {
		// 부모 미션 or 자식 미션 리스트
		List<Mission> missions = missionRepository.findMissionsByTarget(user.getType());

		return new ArrayList<UserMission>();
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
}
