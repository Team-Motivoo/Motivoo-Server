package sopt.org.motivoo.api.controller.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.mission.dto.request.MissionStatusCommand;

public record MissionStatusRequest(
	@JsonProperty("file_name") String fileName
) {

	public MissionStatusCommand toServiceDto() {
		return new MissionStatusCommand(fileName);
	}
}
