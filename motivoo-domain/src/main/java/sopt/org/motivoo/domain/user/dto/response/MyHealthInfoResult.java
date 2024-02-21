package sopt.org.motivoo.domain.user.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.entity.HealthNote;

@Builder
public record MyHealthInfoResult(
	@JsonProperty("is_exercise") boolean isExercise,
	@JsonProperty("exercise_type") String exerciseType,
	@JsonProperty("exercise_frequency") String exerciseFrequency,
	@JsonProperty("exercise_time") String exerciseTime,
	@JsonProperty("health_notes") List<String> healthNotes
) {

	public static MyHealthInfoResult of(Health health) {
		return MyHealthInfoResult.builder()
			.isExercise(health.isExercise())
			.exerciseType(health.getExerciseType().getValue())
			.exerciseFrequency(health.getExerciseFrequency().getValue())
			.exerciseTime(health.getExerciseTime().getValue())
			.healthNotes(health.getHealthNotes().stream()
				.map(HealthNote::getValue).toList()).build();
	}
}
