package sopt.org.motivooServer.domain.mission.dto.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.mission.entity.UserMissionChoices;

@Builder
public record TodayMissionResponse(
	@JsonProperty("is_choice_finished") Boolean isChoiceFinished,
	String date,
	@JsonProperty("mission_choice_list") List<TodayUserMissionDto> missionChoiceList,
	@JsonProperty("today_mission") TodayUserMissionDto todayMission
) {

	public static TodayMissionResponse of(List<UserMissionChoices> missionChoiceList) {
		return TodayMissionResponse.builder()
			.isChoiceFinished(false)
			.date(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.KOREAN)))
			.missionChoiceList(missionChoiceList.stream()
				.map(TodayUserMissionDto::of).toList()).build();
	}

	public static TodayMissionResponse of(UserMission todayMission) {
		return TodayMissionResponse.builder()
			.isChoiceFinished(true)
			.todayMission(TodayUserMissionDto.of(todayMission)).build();
	}
}
