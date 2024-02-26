package sopt.org.motivoo.api.controller.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.mission.dto.response.OpponentGoalStepsResult;

public record OpponentGoalStepsResponse(
	@JsonProperty("opponent_goal_step_count") int opponentGoalStepCount
) {

	public static OpponentGoalStepsResponse of(OpponentGoalStepsResult result) {
		return new OpponentGoalStepsResponse(result.opponentGoalStepCount());
	}
}
