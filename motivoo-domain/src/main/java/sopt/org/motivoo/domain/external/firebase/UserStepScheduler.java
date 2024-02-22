package sopt.org.motivoo.domain.external.firebase;

import static sopt.org.motivoo.common.advice.CommonExceptionType.*;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.common.advice.BusinessException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserStepScheduler {

	private final FirebaseService firebaseService;

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
	public void readUserStep() {
		log.info("유저 걸음 수 읽기 연산 스케줄러 실행");
		try {
			firebaseService.selectAllUserStep();
			log.info("스케줄러에서 유저 select 성공");
		} catch (Exception e) {
			log.error("파이어베이스 스케줄러 실행 중 발생한 에러: {}", e.getMessage());
			throw new BusinessException(FIREBASE_DB_READ_ERROR);
		}
	}
}
