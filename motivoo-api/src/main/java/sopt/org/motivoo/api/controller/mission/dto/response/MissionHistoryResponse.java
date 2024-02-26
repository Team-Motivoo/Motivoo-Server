package sopt.org.motivoo.api.controller.mission.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.dto.response.MissionHistoryResult;

@Builder
public record MissionHistoryResponse(
	@JsonProperty("user_type") String userType,
	@JsonProperty("today_mission") TodayUserMissionResponse todayMission,
	@JsonProperty("mission_history") List<ParentchildMissionResponse> missionHistory
) {


	public static MissionHistoryResponse of(MissionHistoryResult result) {

		return MissionHistoryResponse.builder()
			.userType(result.userType())
			.todayMission(TodayUserMissionResponse.of(result.todayMission()))
			.missionHistory(result.missionHistory().stream().map(ParentchildMissionResponse::of)
				.collect(Collectors.toList())).build();
	}

}
