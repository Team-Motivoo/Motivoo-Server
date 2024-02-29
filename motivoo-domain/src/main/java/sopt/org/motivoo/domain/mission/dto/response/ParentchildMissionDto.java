package sopt.org.motivoo.domain.mission.dto.response;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.entity.UserMission;

@Builder
public record ParentchildMissionDto(
	String date,
	String myMissionContent,
	String myMissionImgUrl,
	String myMissionStatus,
	String opponentMissionContent,
	String opponentMissionImgUrl,
	String opponentMissionStatus
) {

	public static ParentchildMissionDto of(UserMission myMission, UserMission opponentMission) {
		return ParentchildMissionDto.builder()
			.date(myMission.getCreatedAt().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.KOREAN)))
			.myMissionContent(myMission.getMission().getContent())
			.myMissionImgUrl(myMission.getImgUrl())
			.myMissionStatus(myMission.getCompletedStatus().getValue())
			.opponentMissionContent(opponentMission.getMission().getContent())
			.opponentMissionImgUrl(opponentMission.getImgUrl())
			.opponentMissionStatus(opponentMission.getCompletedStatus().getValue()).build();
	}
}
