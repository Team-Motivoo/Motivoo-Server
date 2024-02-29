package sopt.org.motivoo.domain.user.dto.response;

import java.util.List;

import lombok.Builder;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.entity.HealthNote;

@Builder
public record MyHealthInfoResult(
	boolean isExercise,
	String exerciseType,
	String exerciseFrequency,
	String exerciseTime,
	List<String> healthNotes
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
