package sopt.org.motivoo.domain.mission.dto.response;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;

@Builder
public record TodayUserMissionDto(
	Long missionId,
	String missionContent,
	String missionDescription,
	Integer missionStepCount,
	String missionQuest,
	String missionIconUrl,
	String date
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
			.missionContent(userMission.getMission().getContent())
			.date(userMission.getCreatedAt().format(
				DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.KOREAN))).build();
	}

	public static TodayUserMissionDto of(UserMissionChoices userMissionChoices) {
		return TodayUserMissionDto.builder()
			.missionId(userMissionChoices.getMission().getId())
			.missionContent(userMissionChoices.getMission().getContent())
			.missionIconUrl(userMissionChoices.getMission().getIconUrl()).build();
	}
}
