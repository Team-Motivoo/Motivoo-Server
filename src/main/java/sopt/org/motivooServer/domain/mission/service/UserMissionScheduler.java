package sopt.org.motivooServer.domain.mission.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.mission.entity.CompletedStatus;
import sopt.org.motivooServer.domain.mission.repository.UserMissionRepository;
import sopt.org.motivooServer.global.util.s3.S3Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMissionScheduler {

	private final UserMissionRepository userMissionRepository;
	private final S3Service s3Service;

	@Scheduled(cron = "@daily", zone = "Asia/Seoul")
	public void setCompletedStatus() {

		// 1. 미션 달성 상태 반영
		userMissionRepository.findUserMissionsByCreatedAt(LocalDate.now().minusDays(1)).stream()
			.filter(um -> um.getCompletedStatus() != CompletedStatus.SUCCESS)
			.forEach(um -> um.updateCompletedStatus(CompletedStatus.FAIL));

		// 2. 새로운 미션 선택지
		userMissionRepository.findAll().stream()
			.filter(um -> !um.getUser().getUserMissionChoice().isEmpty())
			.forEach(um -> um.getUser().clearPreUserMissionChoice());
	}

	// 매일 새벽 4시마다 30일 이전의 사진은 버킷에서 삭제한다
	@Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
	public void deleteImgBefore30Days() {
		LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
		userMissionRepository.findUserMissionsByCreatedAtBefore(thirtyDaysAgo)
			.forEach(um -> s3Service.deleteImage(um.getImgUrl()));
	}
}
