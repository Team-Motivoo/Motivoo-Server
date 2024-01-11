package sopt.org.motivooServer.domain.user.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivooServer.domain.health.entity.Health;
import sopt.org.motivooServer.domain.health.entity.HealthNote;

@Builder
public record MyHealthInfoResponse(
	@JsonProperty("is_exercise") boolean isExercise,
	@JsonProperty("exercise_type") String exerciseType,
	@JsonProperty("exercise_frequency") String exerciseFrequency,
	@JsonProperty("exercise_time") String exerciseTime,
	@JsonProperty("health_notes") List<String> healthNotes
) {

	public static MyHealthInfoResponse of(Health health) {
		return MyHealthInfoResponse.builder()
			.isExercise(health.getIsExercise())
			.exerciseType(health.getExerciseType().getValue())
			.exerciseFrequency(health.getExerciseFrequency().getValue())
			.exerciseTime(health.getExerciseTime().getValue())
			.healthNotes(health.getHealthNotes().stream()
				.map(HealthNote::getValue).toList()).build();
	}
}
