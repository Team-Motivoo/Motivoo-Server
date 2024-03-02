package sopt.org.motivoo.batch.service.scheduler;

import static sopt.org.motivoo.common.advice.CommonExceptionType.*;
import static sopt.org.motivoo.domain.mission.entity.CompletedStatus.*;

import java.util.List;
import java.util.Map;

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
import sopt.org.motivoo.batch.service.firebase.FirebaseService;
import sopt.org.motivoo.common.advice.BusinessException;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.service.UserMissionService;
import sopt.org.motivoo.domain.user.entity.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserStepScheduler {

	private final FirebaseService firebaseService;
	private final UserMissionService userMissionService;

	private final PlatformTransactionManager transactionManager;  // 수동 트랜잭션 처리를 위한 주입

	@PersistenceContext
	private EntityManager em;


	// @Scheduled(cron = "* */2 * * * *", zone = "Asia/Seoul")
	@Scheduled(cron = "@daily", zone = "Asia/Seoul")
	public void flagUserStepInitialize() {
		log.info("유저 걸음 수 초기화 스케줄러 실행");
		try {
			firebaseService.insertUserStep();
			log.info("스케줄러에서 유저 insert 성공");
			// firebaseService.selectUser();
		} catch (Exception e) {
			log.error("파이어베이스 스케줄러 실행 중 발생한 에러: {}", e.getMessage());
			throw new BusinessException(FIREBASE_DB_INSERT_ERROR);
		}
	}

	// @Scheduled(cron = "* */2 * * * *", zone = "Asia/Seoul")
	@Scheduled(fixedRate = 5000, zone = "Asia/Seoul")
	public void readUserStep() {
		log.info("유저 걸음 수 읽기 연산&상태 업데이트 스케줄러 실행");
		try {
			// firebaseService.selectAllUserStep();
			Map<User, Integer> userGoalSteps = userMissionService.getUsersGoalStep();
			List<Long> ids = userGoalSteps.keySet().stream()
				.map(User::getId)
				.toList();
			// DataSnapshot dataSnapshot = (DataSnapshot)firebaseService.selectUserStep(ids);
			Map<String, Integer> result = firebaseService.selectUserStep(ids);

			for (User user : userGoalSteps.keySet()) {
				Long id = user.getId();
				if (result.containsKey(String.valueOf(id)) &&
				result.get(String.valueOf(id)) >= userGoalSteps.get(user)) {
					// 수동 트랜잭션 처리
					TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
					TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

					log.info("목표 걸음 수 {}를 넘은 현재 걸음 수 {}", userGoalSteps.get(user), result.get(String.valueOf(id)));
					try {
						user.getCurrentUserMission().updateCompletedStatus(STEP_COMPLETED);
						UserMission u = em.merge(user.getCurrentUserMission());
						log.info("User 미션 상태 반영 완료: {}", u.getCompletedStatus());
						transactionManager.commit(transactionStatus);
					} catch (PessimisticLockingFailureException | PessimisticLockException e) {
						transactionManager.rollback(transactionStatus);
					} finally {
						em.close();
					}
				}
			}

			log.info("스케줄러에서 유저 select 성공");
		} catch (Exception e) {
			log.error("파이어베이스 스케줄러 실행 중 발생한 에러: {}", e.getMessage());
			throw new BusinessException(FIREBASE_DB_READ_ERROR);
		}
	}
}
