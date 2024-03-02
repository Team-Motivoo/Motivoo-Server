package sopt.org.motivoo.batch.service.scheduler;

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

import sopt.org.motivoo.domain.mission.entity.CompletedStatus;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.mission.entity.MissionQuest;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.repository.MissionQuestRetriever;
import sopt.org.motivoo.domain.mission.repository.MissionRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionChoicesRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionRetriever;
import sopt.org.motivoo.domain.mission.service.UserMissionManager;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.repository.UserRetriever;
import sopt.org.motivoo.external.s3.S3Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMissionScheduler {

	private final UserMissionRetriever userMissionRetriever;
	private final UserMissionChoicesRetriever userMissionChoicesRetriever;
	private final UserRetriever userRetriever;
	private final MissionRetriever missionRetriever;
	private final MissionQuestRetriever missionQuestRetriever;

	private final UserMissionManager userMissionManager;

	private final S3Service s3Service;
	private final PlatformTransactionManager transactionManager;  // 수동 트랜잭션 처리를 위한 주입

	@PersistenceContext
	private EntityManager em;


	// 1. 미션 달성 상태 반영
	@Scheduled(cron = "@daily", zone = "Asia/Seoul")
	// @Scheduled(cron = "* */2 * * * *", zone = "Asia/Seoul")
	public void setCompletedStatus() {
		log.info("미션 달성상태 업데이트 스케줄러 진입");

		List<UserMission> missionsByCreatedAt = userMissionRetriever.getUserMissionsByCreatedDt(LocalDate.now().minusDays(1));
		log.info("어제의 UserMission 개수: {}개", missionsByCreatedAt.size());
		for (UserMission userMission : missionsByCreatedAt) {

			// 수동 트랜잭션 처리
			TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
			TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

			log.info(userMission.getMission().getContent());
			log.info(userMission.getCompletedStatus().getValue());
			if (userMission.getImgUrl() != null) {
				try {
					log.info("이미지가 올라왔으니 성공이어라..");
					userMission.updateCompletedStatus(CompletedStatus.SUCCESS);

					UserMission um = em.merge(userMission);
					log.info("성공 상태로 업데이트 완료: {}", um.getCompletedStatus());
					transactionManager.commit(transactionStatus);
				} catch (PessimisticLockingFailureException | PessimisticLockException e) {
					transactionManager.rollback(transactionStatus);
				} finally {
					em.close();
				}
			}

			if (!userMission.isEmptyUserMission() &&
				(userMission.getCompletedStatus() != CompletedStatus.SUCCESS || userMission.getImgUrl() == null)) {
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


	// 자정마다 오늘의 미션 초기값 bulk insert
	@Scheduled(cron = "@daily", zone = "Asia/Seoul")
	// @Scheduled(cron = "* */2 * * * *", zone = "Asia/Seoul")
	public void insertEmptyUserMission() {
		List<User> users = userRetriever.findAll().stream()
			.filter(user -> !user.isDeleted()).toList();
		Mission emptyMission = missionRetriever.getEmptyMission();
		MissionQuest missionQuest = missionQuestRetriever.getRandomMissionQuest();

		userMissionRetriever.bulkSaveInitUserMission(users, LocalDate.now(), emptyMission, missionQuest);
	}


	// 매일 새벽 4시마다 30일 이전의 사진은 버킷에서 삭제한다
	@Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
	public void deleteImgBefore30Days() {
		log.info("30일 동안 보관한 이미지는 삭제");
		LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
		userMissionRetriever.getUserMissionsByCreatedAtBefore(thirtyDaysAgo)
			.forEach(um -> s3Service.deleteImage(um.getImgUrl()));
	}

	//TODO UserMissionChoice DB 초기화
}
