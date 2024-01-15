package sopt.org.motivooServer.domain.mission.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.mission.entity.CompletedStatus;
import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.mission.repository.UserMissionRepository;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.repository.UserRepository;
import sopt.org.motivooServer.global.external.s3.S3Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMissionScheduler {

	private final UserMissionRepository userMissionRepository;
	private final UserRepository userRepository;

	private final S3Service s3Service;
	private final PlatformTransactionManager transactionManager;  // 수동 트랜잭션 처리를 위한 주입

	@PersistenceContext
	private EntityManager em;

	// 1. 미션 달성 상태 반영
	@Scheduled(cron = "@daily", zone = "Asia/Seoul")
	// @Scheduled(cron = "* */2 * * * *", zone = "Asia/Seoul")
	public void setCompletedStatus() {
		log.info("미션 달성상태 업데이트 스케줄러 진입");

		List<UserMission> missionsByCreatedAt = userMissionRepository.findUserMissionsByCreatedAt(
			LocalDate.now().minusDays(1));
		log.info("어제의 UserMission 개수: {}개", missionsByCreatedAt.size());
		for (UserMission userMission : missionsByCreatedAt) {

			TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
			TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

			log.info(userMission.getMission().getContent());
			log.info(userMission.getCompletedStatus().getValue());
			if (userMission.getCompletedStatus() != CompletedStatus.SUCCESS) {
				try {
					log.info("성공이 아니니 실패여라..");
					userMission.updateCompletedStatus(CompletedStatus.FAIL);
					UserMission um = em.merge(userMission);
					log.info("실패 상태로 업데이트 완료: {}", um.getCompletedStatus());
					transactionManager.commit(transactionStatus);
				} catch (PessimisticLockingFailureException | PessimisticLockException e) {
					transactionManager.rollback(transactionStatus);
				} finally {
					em.close();
				}
			}
		}
	}

	// 2. 오늘의 미션 선택지가 구성되었지만, 선정하지 않고 그냥 넘어간 경우
	// @Scheduled(cron = "* */2 * * * *", zone = "Asia/Seoul")
	@Scheduled(cron = "@daily", zone = "Asia/Seoul")
	public void setClearPreUserMissionChoices() {
		log.info("미션 선택지 리스트 초기화 스케줄러 진입");

		List<User> users = userRepository.findAll();
		log.info("모든 User 개수: {}개", users.size());
		for (User user : users) {

			TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
			TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

			log.info("User {}의 임시 오늘의 미션 선택지 개수: {}개", user.getNickname(), user.getUserMissionChoice().size());

			if (!user.getUserMissionChoice().isEmpty()) {
				try {
					log.info("오늘의 미션 선택지 리스트 개수: {}개", user.getUserMissionChoice().size());
					user.clearPreUserMissionChoice();
					User u = em.merge(user);
					log.info("Clear 후 오늘의 미션 선택지 리스트 개수: {}개", u.getUserMissionChoice().size());
					transactionManager.commit(transactionStatus);
				} catch (PessimisticLockingFailureException | PessimisticLockException e) {
					transactionManager.rollback(transactionStatus);
				} finally {
					em.close();
				}
			}
		}

	}

	// 매일 새벽 4시마다 30일 이전의 사진은 버킷에서 삭제한다
	@Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
	public void deleteImgBefore30Days() {
		LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
		userMissionRepository.findUserMissionsByCreatedAtBefore(thirtyDaysAgo)
			.forEach(um -> s3Service.deleteImage(um.getImgUrl()));
	}

	//TODO UserMissionChoice DB 초기화
}
