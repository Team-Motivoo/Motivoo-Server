package sopt.org.motivoo.api.controller.mission.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.dto.response.TodayMissionResult;

@Builder
public record TodayMissionResponse(
	@JsonProperty("is_choice_finished") Boolean isChoiceFinished,
	String date,
	@JsonProperty("mission_choice_list") List<TodayUserMissionResponse> missionChoiceList,
	@JsonProperty("today_mission") TodayUserMissionResponse todayMission
) {


	public static TodayMissionResponse of(TodayMissionResult result) {
		return TodayMissionResponse.builder()
			.isChoiceFinished(result.isChoiceFinished())
			.date(result.date())
			.missionChoiceList(result.missionChoiceList().stream()
				.map(TodayUserMissionResponse::of).toList())
			.todayMission(TodayUserMissionResponse.of(result.todayMission())).build();
	}
}
