package sopt.org.motivooServer.domain.mission.dto.response;

import lombok.Builder;
import sopt.org.motivooServer.domain.mission.entity.UserMission;

@Builder
public record TodayUserMissionDto(
	String content
) {
	public static TodayUserMissionDto of(UserMission userMission) {
		return TodayUserMissionDto.builder()
			.content(userMission.getMission().getContent()).build();
	}
}
