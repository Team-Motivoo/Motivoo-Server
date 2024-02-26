package sopt.org.motivoo.api.controller.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.dto.response.GoalStepResult;

@Builder
public record GoalStepResponse(
	@JsonProperty("original_step_count") int originalStepCount,
	@JsonProperty("changed_step_count") int changedStepCount
) {

	public static GoalStepResponse of(GoalStepResult result) {
		return GoalStepResponse.builder()
			.originalStepCount(result.originalStepCount())
			.changedStepCount(result.changedStepCount()).build();
	}
}
