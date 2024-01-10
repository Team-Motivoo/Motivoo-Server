package sopt.org.motivooServer.domain.mission.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivooServer.domain.mission.entity.UserMission;

@Builder
public record TodayMissionResponse(
	@JsonProperty("is_choice_finished") Boolean isChoiceFinished,
	@JsonProperty("mission_choice_list") List<TodayUserMissionDto> missionChoiceList,
	@JsonProperty("today_mission") TodayUserMissionDto todayMission
) {

	public static TodayMissionResponse of(List<UserMission> missionChoiceList, UserMission todayMission) {
		return TodayMissionResponse.builder()
			.isChoiceFinished(todayMission==null)
			.missionChoiceList(missionChoiceList.stream().map(TodayUserMissionDto::of).toList())
			.todayMission(TodayUserMissionDto.of(todayMission)).build();
	}
}
