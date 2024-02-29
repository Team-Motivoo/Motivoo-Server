package sopt.org.motivoo.domain.mission.dto.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;

@Builder
public record TodayMissionResult(
	Boolean isChoiceFinished,
	String date,
	List<TodayUserMissionDto> missionChoiceList,
	TodayUserMissionDto todayMission
) {

	public static TodayMissionResult of(List<UserMissionChoices> missionChoiceList) {
		return TodayMissionResult.builder()
			.isChoiceFinished(false)
			.date(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.KOREAN)))
			.missionChoiceList(missionChoiceList.stream()
				.map(TodayUserMissionDto::of).toList()).build();
	}

	public static TodayMissionResult of(UserMission todayMission) {
		return TodayMissionResult.builder()
			.isChoiceFinished(true)
			.todayMission(TodayUserMissionDto.of(todayMission)).build();
	}
}
