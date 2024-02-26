package sopt.org.motivoo.domain.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record TodayMissionChoiceCommand(
	@JsonProperty("mission_id") Long missionId
) {
}
