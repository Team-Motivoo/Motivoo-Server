package sopt.org.motivoo.domain.mission.dto.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
public record MissionHistoryResult(
	String userType,
	String opponentUserType,
	Boolean isTodayMissionChoiceCompleted,
	TodayUserMissionDto todayMission,
	List<ParentchildMissionDto> missionHistory
) {


	public static MissionHistoryResult of(User user, User opponentUser, UserMission todayMission, Map<LocalDate, List<UserMission>> missionGroupsByDate) {

		List<ParentchildMissionDto> parentchildMissions = getParentchildMissionDtos(user, missionGroupsByDate);

		return MissionHistoryResult.builder()
			.userType(user.getType().getValue())
			.opponentUserType(opponentUser.getType().getValue())
			.todayMission(TodayUserMissionDto.ofHistory(todayMission))
			.missionHistory(parentchildMissions).build();
	}

	public static MissionHistoryResult of(User user, User opponentUser, Map<LocalDate, List<UserMission>> missionGroupsByDate) {

		List<ParentchildMissionDto> parentchildMissions = getParentchildMissionDtos(user, missionGroupsByDate);

		return MissionHistoryResult.builder()
			.userType(user.getType().getValue())
			.opponentUserType(opponentUser.getType().getValue())
			.missionHistory(parentchildMissions).build();
	}

	public static MissionHistoryResult of(User myUser, User opponentUser) {
		return MissionHistoryResult.builder()
			.userType(myUser.getType().getValue())
			.opponentUserType(opponentUser.getType().getValue()).build();
	}

	@NotNull
	private static List<ParentchildMissionDto> getParentchildMissionDtos(User user, Map<LocalDate, List<UserMission>> missionGroupsByDate) {

		List<ParentchildMissionDto> parentchildMissions = new ArrayList<>(missionGroupsByDate.size());

		for (Map.Entry<LocalDate, List<UserMission>> entry : missionGroupsByDate.entrySet()) {
			if (user.equals(entry.getValue().get(0).getUser())) {
				parentchildMissions.add(ParentchildMissionDto.of(entry.getValue().get(0), entry.getValue().get(1)));
			} else {
				parentchildMissions.add(ParentchildMissionDto.of(entry.getValue().get(1), entry.getValue().get(0)));
			}
		}
		return parentchildMissions;
	}


	// Test용
	public static MissionHistoryResult of(User user, User opponentUser, UserMission todayMission, List<UserMission> myMissions, List<UserMission> opponentMissions) {

		return MissionHistoryResult.builder()
			.userType(user.getType().getValue())
			.opponentUserType(opponentUser.getType().getValue())
			.todayMission(TodayUserMissionDto.ofHistory(todayMission))
			.missionHistory(IntStream.range(0, myMissions.size())
				.mapToObj(i -> ParentchildMissionDto.of(myMissions.get(i), opponentMissions.get(i)))
				.collect(Collectors.toList())).build();
	}

}
