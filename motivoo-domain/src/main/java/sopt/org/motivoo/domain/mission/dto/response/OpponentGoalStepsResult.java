package sopt.org.motivoo.domain.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpponentGoalStepsResult(
	@JsonProperty("opponent_goal_step_count") int opponentGoalStepCount
) {

	public static OpponentGoalStepsResult of(int opponentGoalStepCount) {
		return new OpponentGoalStepsResult(opponentGoalStepCount);
	}
}
