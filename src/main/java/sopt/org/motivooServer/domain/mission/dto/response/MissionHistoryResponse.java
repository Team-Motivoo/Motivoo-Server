package sopt.org.motivooServer.domain.mission.dto.response;

import java.time.LocalDate;
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
		List<UserMission> my = new ArrayList<>();
		List<UserMission> opponent = new ArrayList<>();

		// createdAt 필드를 기준으로 동일하지 않은 날짜는 null로 대응시키기
		for (UserMission myMission : myMissions) {
			LocalDate myDate = myMission.getCreatedAt().toLocalDate();
			UserMission opponentMission = opponentMissions.stream()
				.filter(om -> myDate.equals(om.getCreatedAt().toLocalDate()))
				.findFirst()
				.orElse(null);

			if (opponentMission != null) {
				my.add(myMission);
				opponent.add(opponentMission);
			} else {
				my.add(myMission);
				opponent.add(null);
			}
		}

		return MissionHistoryResponse.builder()
			.userType(user.getType().getValue())
			.todayMission(TodayUserMissionDto.ofHistory(todayMission))
			.missionHistory(IntStream.range(0, myMissions.size())
				.mapToObj(i -> ParentchildMissionDto.of(my.get(i), opponent.get(i)))
				.collect(Collectors.toList()))
			.build();
	}

}
