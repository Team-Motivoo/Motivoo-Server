package sopt.org.motivoo.domain.external.firebase;

import static sopt.org.motivoo.common.advice.CommonExceptionType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.firebase.database.DataSnapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.common.advice.BusinessException;
import sopt.org.motivoo.domain.mission.entity.CompletedStatus;
import sopt.org.motivoo.domain.mission.service.UserMissionService;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.repository.UserRetriever;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserStepScheduler {

	private final FirebaseService firebaseService;
	private final UserMissionService userMissionService;

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
				if (result.get(String.valueOf(id)) >= userGoalSteps.get(id)) {
					log.info("목표 걸음 수 {}를 넘은 현재 걸음 수 {}", userGoalSteps.get(id), result.get(String.valueOf(id)));
					user.getCurrentUserMission().updateCompletedStatus(CompletedStatus.STEP_COMPLETED);
				}
			}

			log.info("스케줄러에서 유저 select 성공");
		} catch (Exception e) {
			log.error("파이어베이스 스케줄러 실행 중 발생한 에러: {}", e.getMessage());
			throw new BusinessException(FIREBASE_DB_READ_ERROR);
		}
	}
}
