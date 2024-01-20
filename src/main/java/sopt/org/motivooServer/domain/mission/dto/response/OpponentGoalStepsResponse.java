package sopt.org.motivooServer.domain.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpponentGoalStepsResponse(
	@JsonProperty("opponent_goal_step_count") int opponentGoalStepCount
) {

	public static OpponentGoalStepsResponse of(int opponentGoalStepCount) {
		return new OpponentGoalStepsResponse(opponentGoalStepCount);
	}
}
