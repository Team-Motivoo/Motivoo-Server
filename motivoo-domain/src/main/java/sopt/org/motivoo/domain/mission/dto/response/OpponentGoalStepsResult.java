package sopt.org.motivoo.domain.mission.dto.response;

public record OpponentGoalStepsResult(
	Integer opponentGoalStepCount
) {

	public static OpponentGoalStepsResult of(int opponentGoalStepCount) {
		return new OpponentGoalStepsResult(opponentGoalStepCount);
	}
}
