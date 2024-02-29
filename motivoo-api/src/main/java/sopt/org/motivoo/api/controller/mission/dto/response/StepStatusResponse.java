package sopt.org.motivoo.api.controller.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.dto.response.StepStatusResult;

@Builder
public record StepStatusResponse(
	@JsonProperty("user_type") String userType,
	@JsonProperty("user_id") Long userId,
	@JsonProperty("user_goal_step_count") Integer userGoalStepCount,
	@JsonProperty("opponent_user_id") Long opponentUserId,
	@JsonProperty("opponent_user_goal_step_count") Integer opponentUserGoalStepCount,
	@JsonProperty("is_step_count_completed") Boolean isStepCountCompleted,
	@JsonProperty("is_opponent_user_withdraw") Boolean isOpponentUserWithdraw,
	@JsonProperty("is_mission_img_completed") Boolean isMissionImgCompleted
) {

	public static StepStatusResponse of(StepStatusResult result) {
		return StepStatusResponse.builder()
			.userType(result.userType())
			.userId(result.userId())
			.userGoalStepCount(result.userGoalStepCount())
			.opponentUserId(result.opponentUserId())
			.opponentUserGoalStepCount(result.opponentUserGoalStepCount())
			.isStepCountCompleted(result.isStepCountCompleted())
			.isOpponentUserWithdraw(result.isOpponentUserWithdraw())
			.isMissionImgCompleted(result.isMissionImgCompleted()).build();
	}
}
