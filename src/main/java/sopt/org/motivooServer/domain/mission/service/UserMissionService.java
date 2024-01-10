package sopt.org.motivooServer.domain.mission.service;

import static java.util.Objects.*;
import static sopt.org.motivooServer.domain.mission.entity.CompletedStatus.*;
import static sopt.org.motivooServer.domain.mission.exception.MissionExceptionType.*;
import static sopt.org.motivooServer.domain.parentchild.exception.ParentchildExceptionType.*;
import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.mission.dto.request.MissionImgUrlRequest;
import sopt.org.motivooServer.domain.mission.dto.request.TodayMissionChoiceRequest;
import sopt.org.motivooServer.domain.mission.dto.response.MissionHistoryResponse;
import sopt.org.motivooServer.domain.mission.dto.response.MissionImgUrlResponse;
import sopt.org.motivooServer.domain.mission.dto.response.TodayMissionResponse;
import sopt.org.motivooServer.domain.mission.entity.Mission;
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
		User opponentUser = userRepository.findByIdAndParentchild(userId, myUser.getParentchild()).orElseThrow(
			() -> new ParentchildException(NOT_EXIST_PARENTCHILD_USER));

		UserMission todayMission = userMissionRepository.findFirstByUserOrderByCreatedAt(myUser).orElseThrow(
			() -> new MissionException(EMPTY_USER_MISSIONS));

		validateTodayDateMission(todayMission);

		return MissionHistoryResponse.of(myUser, todayMission,
			userMissionRepository.findUserMissionsByUserOrderByCreatedAt(myUser),
			userMissionRepository.findUserMissionsByUserOrderByCreatedAt(opponentUser));
	}

	public TodayMissionResponse getTodayMission(final Long userId) {
		User user = getUserById(userId);
		List<UserMission> todayMission = filterTodayUserMission(user);

	}

	private List<UserMission> filterTodayUserMission(User user) {
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

	private static void validateTodayDateMission(UserMission todayMission) {
		if (!todayMission.getCreatedAt().equals(LocalDate.now())) {
			log.info("오늘 날짜와 동일하지 않은 최근 미션!");
			throw new MissionException(NOT_CHOICE_TODAY_MISSION);
		}
	}

	private User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			() -> new UserException(USER_NOT_FOUND)
		);
	}

	private Mission getMissionById(Long missionId) {
		return missionRepository.findById(missionId).orElseThrow(
			() -> new MissionException(MISSION_NOT_FOUND));
	}

	private UserMission getUserMission(Long userMissionId) {
		UserMission userMission = userMissionRepository.findById(userMissionId).orElseThrow(
			() -> new MissionException(USER_MISSION_NOT_FOUND)
		);
		return userMission;
	}
}
