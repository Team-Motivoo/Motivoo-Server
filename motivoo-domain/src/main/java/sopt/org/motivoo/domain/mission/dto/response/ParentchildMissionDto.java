package sopt.org.motivoo.domain.mission.dto.response;

import static sopt.org.motivoo.domain.mission.entity.CompletedStatus.*;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.entity.CompletedStatus;
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
		CompletedStatus myMissionStatus = myMission.getCompletedStatus();
		CompletedStatus opponentMissionStatus = opponentMission.getCompletedStatus();

		// 출력상으로는 목표 걸음 수만 달성한 상태를 진행중으로 판단
		if (myMissionStatus.equals(STEP_COMPLETED)) {
			myMissionStatus = IN_PROGRESS;
		}
		if (opponentMissionStatus.equals(STEP_COMPLETED)) {
			opponentMissionStatus = IN_PROGRESS;
		}
		return ParentchildMissionDto.builder()
			.date(myMission.getCreatedAt().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.KOREAN)))
			.myMissionContent(myMission.getMission().getContent())
			.myMissionImgUrl(myMission.getImgUrl())
			.myMissionStatus(myMissionStatus.getValue())
			.opponentMissionContent(opponentMission.getMission().getContent())
			.opponentMissionImgUrl(opponentMission.getImgUrl())
			.opponentMissionStatus(opponentMissionStatus.getValue()).build();
	}
}
