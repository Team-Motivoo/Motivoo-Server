package sopt.org.motivoo.domain.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodayUserMissionDto(
	@JsonProperty("mission_id") Long missionId,
	@JsonProperty("mission_content") String missionContent,
	@JsonProperty("mission_description") String missionDescription,
	@JsonProperty("mission_step_count") Integer missionStepCount,
	@JsonProperty("mission_quest") String missionQuest,
	@JsonProperty("mission_icon_url") String missionIconUrl
) {
	public static TodayUserMissionDto of(UserMission userMission) {
		return TodayUserMissionDto.builder()
			.missionContent(userMission.getMission().getContent())
			.missionDescription(userMission.getMission().getDescriptionUrl())
			.missionStepCount(userMission.getMission().getStepCount())
			.missionQuest(userMission.getMissionQuest().getContent()).build();
	}

	public static TodayUserMissionDto ofHistory(UserMission userMission) {
		return TodayUserMissionDto.builder()
			.missionContent(userMission.getMission().getContent()).build();
	}

	public static TodayUserMissionDto of(UserMissionChoices userMissionChoices) {
		return TodayUserMissionDto.builder()
			.missionId(userMissionChoices.getMission().getId())
			.missionContent(userMissionChoices.getMission().getContent())
			.missionIconUrl(userMissionChoices.getMission().getIconUrl()).build();
	}
}
