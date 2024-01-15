package sopt.org.motivooServer.domain.mission.dto.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivooServer.domain.user.entity.User;

@Builder
public record MissionStepStatusResponse(
	String date,
	@JsonProperty("user_type") String userType,
	@JsonProperty("user_id") Long userId,
	@JsonProperty("user_goal_step_count") Integer userGoalStepCount,
	@JsonProperty("opponent_user_id") Long opponentUserId,
	@JsonProperty("opponent_user_goal_step_count") Integer opponentUserGoalStepCount,
	@JsonProperty("is_step_count_completed") Boolean isStepCountCompleted,
	@JsonProperty("is_opponent_user_withdraw") Boolean isOpponentUserWithdraw
) {

	public static MissionStepStatusResponse of(User user, User opponentUser, Boolean status) {
		return MissionStepStatusResponse.builder()
			.date(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)))
			.userType(user.getType().getValue())
			.userId(user.getId())
			.userGoalStepCount(user.getCurrentUserMission().getMission().getStepCount())
			.opponentUserId(opponentUser.getId())
			.opponentUserGoalStepCount(opponentUser.getCurrentUserMission().getMission().getStepCount())
			.isStepCountCompleted(status)
			.isOpponentUserWithdraw(opponentUser.isDeleted()).build();
	}
}
