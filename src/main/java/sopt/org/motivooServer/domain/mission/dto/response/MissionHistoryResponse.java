package sopt.org.motivooServer.domain.mission.dto.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivooServer.domain.mission.entity.Mission;
import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.user.entity.User;

@Builder
public record MissionHistoryResponse(
	@JsonProperty("user_type") String userType,
	@JsonProperty("today_mission") TodayUserMissionDto todayMission,
	@JsonProperty("mission_history") List<ParentchildMissionDto> missionHistory
) {


	public static MissionHistoryResponse of(User user, UserMission todayMission, Map<LocalDate, List<UserMission>> missionGroupsByDate) {

		List<ParentchildMissionDto> parentchildMissions = new ArrayList<>(missionGroupsByDate.size());

		for (Map.Entry<LocalDate, List<UserMission>> entry : missionGroupsByDate.entrySet()) {
			if (user.equals(entry.getValue().get(0).getUser())) {
				parentchildMissions.add(ParentchildMissionDto.of(entry.getValue().get(0), entry.getValue().get(1)));
			} else {
				parentchildMissions.add(ParentchildMissionDto.of(entry.getValue().get(1), entry.getValue().get(0)));
			}
		}
		return MissionHistoryResponse.builder()
			.userType(user.getType().getValue())
			.todayMission(TodayUserMissionDto.ofHistory(todayMission))
			.missionHistory(parentchildMissions).build();
	}

	public static MissionHistoryResponse of(User user) {
		return MissionHistoryResponse.builder()
			.userType(user.getType().getValue()).build();
	}

	public static MissionHistoryResponse of(User user, UserMission todayMission, List<UserMission> myMissions, List<UserMission> opponentMissions) {

		return MissionHistoryResponse.builder()
			.userType(user.getType().getValue())
			.todayMission(TodayUserMissionDto.ofHistory(todayMission))
			.missionHistory(IntStream.range(0, myMissions.size())
				.mapToObj(i -> ParentchildMissionDto.of(myMissions.get(i), opponentMissions.get(i)))
				.collect(Collectors.toList()))
			.build();
	}

}
