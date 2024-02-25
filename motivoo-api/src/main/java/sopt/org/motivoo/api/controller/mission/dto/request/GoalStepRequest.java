package sopt.org.motivoo.api.controller.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.mission.dto.request.GoalStepCommand;

public record GoalStepRequest(
	@JsonProperty("goal_step_count")
	int goalStepCount
) {

	public GoalStepCommand toServiceDto() {
		return new GoalStepCommand(goalStepCount);
	}
}
