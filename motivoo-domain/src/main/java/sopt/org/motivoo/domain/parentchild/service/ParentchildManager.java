package sopt.org.motivoo.domain.parentchild.service;

import static sopt.org.motivoo.domain.health.exception.HealthExceptionType.*;
import static sopt.org.motivoo.domain.parentchild.exception.ParentchildExceptionType.*;

import java.util.Random;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.health.dto.request.OnboardingCommand;
import sopt.org.motivoo.domain.health.entity.ExerciseFrequency;
import sopt.org.motivoo.domain.health.entity.ExerciseLevel;
import sopt.org.motivoo.domain.health.entity.ExerciseTime;
import sopt.org.motivoo.domain.health.entity.ExerciseType;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.entity.HealthNote;
import sopt.org.motivoo.domain.health.exception.HealthException;
import sopt.org.motivoo.domain.health.service.CalculateScore;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.parentchild.exception.ParentchildException;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.entity.UserType;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentchildManager {

	private final CalculateScore calculateScore;

	private static final int RANDOM_STR_LEN = 8;

	public Health onboardInput(User user, OnboardingCommand request){

		user.updateOnboardingInfo(UserType.of(request.type()), request.age());

		Health health = createHealthInfo(user, request);
		updateExerciseLevel(health);

		return health;
	}

	public Parentchild createParentchild(User user) {

		Parentchild parentchild = Parentchild.builder()
			.inviteCode(createInviteCode())
			.isMatched(false).build();

		user.updateParentchild(parentchild);
		return parentchild;
	}

	public Health createHealthInfo(User user, OnboardingCommand request) {

		Health health = Health.builder()
			.user(user)
			.isExercise(request.isExercise())
			.exerciseType(ExerciseType.of(request.exerciseType()))
			.exerciseFrequency(ExerciseFrequency.of(request.exerciseCount()))
			.exerciseTime(ExerciseTime.of(request.exerciseTime()))
			.healthNotes(HealthNote.of(request.exerciseNote()))
			.exerciseLevel(ExerciseLevel.BEGINNER).build();

		validateHealthNotes(health);
		return health;
	}

	private static void validateHealthNotes(Health health) {
		// 운동 특이사항 최대 3개까지 선택 가능
		if (health.getHealthNotes().size() > 3) {
			throw new HealthException(EXCEED_HEALTH_NOTES_RANGE);
		}
	}

	private void updateExerciseLevel(Health health) {
		double exerciseScore = calculateScore.calculate(health.isExercise(), health.getExerciseType(),
			health.getExerciseFrequency(), health.getExerciseTime());
		log.info("가중치 결과 점수="+exerciseScore);
		health.updateExerciseLevel(exerciseScore);
	}

	private String createInviteCode(){
		Random random = new Random();
		StringBuilder randomBuf = new StringBuilder();
		for (int i = 0; i < RANDOM_STR_LEN; i++) {
			int randomType = random.nextInt(3); // 0은 소문자, 1은 대문자, 2는 숫자를 나타냄

			switch (randomType) {
				case 0:
					randomBuf.append((char) (random.nextInt(26) + 'a')); // 소문자 추가
					break;
				case 1:
					randomBuf.append((char) (random.nextInt(26) + 'A')); // 대문자 추가
					break;
				case 2:
					randomBuf.append(random.nextInt(10)); // 숫자 추가
					break;
			}
		}
		return randomBuf.toString();
	}


	public static void completeMatching(User user, Parentchild parentchild) {
		user.updateParentchild(parentchild);
		parentchild.matchingSuccess();
	}

	public void validateUserRelation(User user, int cntWithUser) {
		if (user.getParentchild().validateParentchild(cntWithUser) || user.getParentchild().isMatched()) {
			throw new ParentchildException(MATCH_ALREADY_COMPLETED);
		}
	}

	public void validateInviteCode(Parentchild parentchild, int cntWithInviteCode) {
		if (parentchild.validateParentchild(cntWithInviteCode) || parentchild.isMatched()) {
			throw new ParentchildException(ALREADY_FINISHED_INVITE_CODE);
		}
	}
}
