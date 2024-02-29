package sopt.org.motivoo.domain.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record StepStatusCommand(
	@JsonProperty("my_step_count") Integer myStepCount,
	@JsonProperty("opponent_step_count") Integer opponentStepCount
) {
}