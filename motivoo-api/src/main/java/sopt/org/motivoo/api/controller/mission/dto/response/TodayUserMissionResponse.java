package sopt.org.motivoo.api.controller.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.dto.response.TodayUserMissionDto;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodayUserMissionResponse(
	@JsonProperty("mission_id") Long missionId,
	@JsonProperty("mission_content") String missionContent,
	@JsonProperty("mission_description") String missionDescription,
	@JsonProperty("mission_step_count") Integer missionStepCount,
	@JsonProperty("mission_quest") String missionQuest,
	@JsonProperty("mission_icon_url") String missionIconUrl,
	@JsonProperty("date") String date
) {

	public static TodayUserMissionResponse of(TodayUserMissionDto userMission) {
		if (userMission != null) {
			return TodayUserMissionResponse.builder()
				.missionId(userMission.missionId())
				.missionContent(userMission.missionContent())
				.missionDescription(userMission.missionDescription())
				.missionStepCount(userMission.missionStepCount())
				.missionQuest(userMission.missionQuest())
				.missionIconUrl(userMission.missionIconUrl())
				.date(userMission.date()).build();
		}
		return null;
	}

}
