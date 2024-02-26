package sopt.org.motivoo.api.controller.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.mission.dto.request.MissionStepStatusCommand;

public record MissionStepStatusRequest(
	@JsonProperty("my_step_count") Integer myStepCount,
	@JsonProperty("opponent_step_count") Integer opponentStepCount
) {

	public MissionStepStatusCommand toServiceDto() {
		return MissionStepStatusCommand.builder()
			.myStepCount(myStepCount).build();
	}
}