package sopt.org.motivooServer.domain.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivooServer.domain.mission.entity.UserMission;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodayUserMissionDto(
	@JsonProperty("mission_id") Long missionId,
	@JsonProperty("mission_content") String missionContent,
	@JsonProperty("mission_description") String missionDescription
) {
	public static TodayUserMissionDto of(UserMission userMission) {
		return TodayUserMissionDto.builder()
			.missionId(userMission.getId())
			.missionContent(userMission.getMission().getContent())
			.missionDescription(userMission.getMission().getDescriptionUrl()).build();
	}
}
