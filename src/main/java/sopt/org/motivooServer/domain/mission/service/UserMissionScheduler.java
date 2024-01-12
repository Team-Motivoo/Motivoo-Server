package sopt.org.motivooServer.domain.mission.service;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.mission.entity.CompletedStatus;
import sopt.org.motivooServer.domain.mission.repository.UserMissionRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMissionScheduler {

	private final UserMissionRepository userMissionRepository;
	private final UserMissionService userMissionService;

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
}
