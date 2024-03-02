package sopt.org.motivoo.domain.mission.dto.request;

import lombok.Builder;

@Builder
public record TodayMissionChoiceCommand(
	Long missionId
) {
}
