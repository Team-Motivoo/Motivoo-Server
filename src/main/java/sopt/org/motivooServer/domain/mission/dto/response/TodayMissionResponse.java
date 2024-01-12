package sopt.org.motivooServer.domain.mission.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.mission.entity.UserMissionChoices;

@Builder
public record TodayMissionResponse(
	@JsonProperty("is_choice_finished") Boolean isChoiceFinished,
	@JsonProperty("mission_choice_list") List<TodayUserMissionDto> missionChoiceList,
	@JsonProperty("today_mission") TodayUserMissionDto todayMission
) {

	public static TodayMissionResponse of(List<UserMissionChoices> missionChoiceList) {
		return TodayMissionResponse.builder()
			.isChoiceFinished(false)
			.missionChoiceList(missionChoiceList.stream()
				.map(TodayUserMissionDto::of).toList()).build();
	}

	public static TodayMissionResponse of(UserMission todayMission) {
		return TodayMissionResponse.builder()
			.isChoiceFinished(true)
			.todayMission(TodayUserMissionDto.of(todayMission)).build();
	}
}
