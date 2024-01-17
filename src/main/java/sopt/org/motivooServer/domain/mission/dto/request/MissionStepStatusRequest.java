package sopt.org.motivooServer.domain.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MissionStepStatusRequest(
	@JsonProperty("my_step_count") Integer myStepCount,
	@JsonProperty("opponent_step_count") Integer opponentStepCount
) {
}