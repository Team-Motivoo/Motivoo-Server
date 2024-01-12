package sopt.org.motivooServer.domain.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TodayMissionChoiceRequest(
	@JsonProperty("mission_id") Long missionId
) {
}
