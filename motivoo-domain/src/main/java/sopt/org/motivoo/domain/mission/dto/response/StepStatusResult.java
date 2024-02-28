package sopt.org.motivoo.domain.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
public record StepStatusResult(
	@JsonProperty("user_type") String userType,
	@JsonProperty("user_id") Long userId,
	@JsonProperty("user_goal_step_count") Integer userGoalStepCount,
	@JsonProperty("opponent_user_id") Long opponentUserId,
	@JsonProperty("opponent_user_goal_step_count") Integer opponentUserGoalStepCount,
	@JsonProperty("is_step_count_completed") Boolean isStepCountCompleted,
	@JsonProperty("is_opponent_user_withdraw") Boolean isOpponentUserWithdraw,
	@JsonProperty("is_mission_img_completed") Boolean isMissionImgCompleted
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
