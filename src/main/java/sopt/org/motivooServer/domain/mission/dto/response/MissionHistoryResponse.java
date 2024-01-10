package sopt.org.motivooServer.domain.mission.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.user.entity.User;

@Builder
public record MissionHistoryResponse(
	@JsonProperty("user_type") String userType,
	@JsonProperty("today_mission") TodayUserMissionDto todayMission,
	@JsonProperty("mission_history") List<ParentchildMissionDto> missionHistory
) {

	public static MissionHistoryResponse of(User user, UserMission todayMission, List<UserMission> myMissions, List<UserMission> opponentMissions) {
		return MissionHistoryResponse.builder()
			.userType(user.getType().getValue())
			.todayMission(TodayUserMissionDto.of(todayMission))
			.missionHistory(IntStream.range(0, myMissions.size())
				.mapToObj(i -> ParentchildMissionDto.of(myMissions.get(i), opponentMissions.get(i)))
				.collect(Collectors.toList())).build();
	}
}
