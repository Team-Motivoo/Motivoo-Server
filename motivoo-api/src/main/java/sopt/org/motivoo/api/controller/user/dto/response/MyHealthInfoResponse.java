package sopt.org.motivoo.api.controller.user.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.user.dto.response.MyHealthInfoResult;

@Builder
public record MyHealthInfoResponse(
	@JsonProperty("is_exercise") boolean isExercise,
	@JsonProperty("exercise_type") String exerciseType,
	@JsonProperty("exercise_frequency") String exerciseFrequency,
	@JsonProperty("exercise_time") String exerciseTime,
	@JsonProperty("health_notes") List<String> healthNotes
) {

	public static MyHealthInfoResponse of(MyHealthInfoResult result) {
		return MyHealthInfoResponse.builder()
			.isExercise(result.isExercise())
			.exerciseType(result.exerciseType())
			.exerciseFrequency(result.exerciseFrequency())
			.exerciseTime(result.exerciseTime())
			.healthNotes(result.healthNotes()).build();
	}
}
