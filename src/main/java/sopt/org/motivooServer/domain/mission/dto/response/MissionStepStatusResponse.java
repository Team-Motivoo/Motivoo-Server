package sopt.org.motivooServer.domain.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivooServer.domain.user.entity.User;

@Builder
public record MissionStepStatusResponse(
	@JsonProperty("user_type") String userType,
	@JsonProperty("user_id") Long userId,
	@JsonProperty("user_goal_step_count") Integer userGoalStepCount,
	@JsonProperty("opponent_user_id") Long opponentUserId,
	@JsonProperty("opponent_user_goal_step_count") Integer opponentUserGoalStepCount,
	@JsonProperty("is_step_count_completed") Boolean isStepCountCompleted
) {

	public static MissionStepStatusResponse of(User user, User opponentUser, Boolean status) {
		return MissionStepStatusResponse.builder()
			.userType(user.getType().getValue())
			.userId(user.getId())
			.userGoalStepCount(user.getCurrentUserMission().getMission().getStepCount())
			.opponentUserId(opponentUser.getId())
			.opponentUserGoalStepCount(opponentUser.getCurrentUserMission().getMission().getStepCount())
			.isStepCountCompleted(status).build();
	}
}
