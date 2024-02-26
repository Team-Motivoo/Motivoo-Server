package sopt.org.motivoo.api.controller.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.mission.dto.request.TodayMissionChoiceCommand;

public record TodayMissionChoiceRequest(
	@JsonProperty("mission_id") Long missionId
) {

	public TodayMissionChoiceCommand toServiceDto() {
		return TodayMissionChoiceCommand.builder()
			.missionId(missionId).build();
	}
}
