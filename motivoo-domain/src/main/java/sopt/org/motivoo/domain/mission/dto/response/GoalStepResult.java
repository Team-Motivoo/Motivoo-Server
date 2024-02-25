package sopt.org.motivoo.domain.mission.dto.response;

public record GoalStepResult(
	int originalStepCount,
	int changedStepCount
) {

	public static GoalStepResult of(int original, int changed) {
		return new GoalStepResult(original, changed);
	}
}
