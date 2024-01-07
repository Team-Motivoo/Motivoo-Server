package sopt.org.motivooServer.domain.mission.service;

import static sopt.org.motivooServer.domain.mission.exception.MissionExceptionType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.mission.dto.request.MissionImgUrlRequest;
import sopt.org.motivooServer.domain.mission.dto.response.MissionImgUrlResponse;
import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.mission.exception.MissionException;
import sopt.org.motivooServer.domain.mission.repository.UserMissionRepository;
import sopt.org.motivooServer.global.util.s3.PreSignedUrlResponse;
import sopt.org.motivooServer.global.util.s3.S3BucketDirectory;
import sopt.org.motivooServer.global.util.s3.S3Service;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserMissionService {

	private final UserMissionRepository userMissionRepository;
	private final S3Service s3Service;

	@Transactional
	public MissionImgUrlResponse getMissionImgUrl(final MissionImgUrlRequest request, final Long missionId) {
		UserMission userMission = userMissionRepository.findById(missionId).orElseThrow(
			() -> new MissionException(USER_MISSION_NOT_FOUND)
		);

		PreSignedUrlResponse preSignedUrl = s3Service.getUploadPreSignedUrl(
			S3BucketDirectory.of(request.imgPrefix()));
		userMission.updateImgUrl(preSignedUrl.fileName());
		return MissionImgUrlResponse.of(preSignedUrl.url(), preSignedUrl.fileName());
	}
}
