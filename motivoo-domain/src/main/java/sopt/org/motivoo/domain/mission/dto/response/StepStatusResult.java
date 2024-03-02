package sopt.org.motivoo.domain.mission.dto.response;

import lombok.Builder;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
public record StepStatusResult(
	String userType,
	Long userId,
	Integer userGoalStepCount,
	Long opponentUserId,
	Integer opponentUserGoalStepCount,
	Boolean isStepCountCompleted,
	Boolean isOpponentUserWithdraw,
	Boolean isMissionImgCompleted
) {

	public static StepStatusResult of(User user, User opponentUser, int myGoalStep, int opponentGoalStep, Boolean status, Boolean imgCompleted) {
		return StepStatusResult.builder()
			.userType(user.getType().getValue())
			.userId(user.getId())
			.userGoalStepCount(myGoalStep)
			.opponentUserId(opponentUser.getId())
			.opponentUserGoalStepCount(opponentGoalStep)
			.isStepCountCompleted(status)
			.isOpponentUserWithdraw(opponentUser.isDeleted())
			.isMissionImgCompleted(imgCompleted).build();
	}
}
