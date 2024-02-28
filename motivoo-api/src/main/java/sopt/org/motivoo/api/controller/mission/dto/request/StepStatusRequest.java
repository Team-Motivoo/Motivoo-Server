package sopt.org.motivoo.api.controller.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.mission.dto.request.StepStatusCommand;

public record StepStatusRequest(
	@JsonProperty("my_step_count") Integer myStepCount,
	@JsonProperty("opponent_step_count") Integer opponentStepCount
) {

	public StepStatusCommand toServiceDto() {
		return StepStatusCommand.builder()
			.myStepCount(myStepCount)
			.opponentStepCount(opponentStepCount).build();
	}
}