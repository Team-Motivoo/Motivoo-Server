package sopt.org.motivoo.domain.mission.dto.request;

import lombok.Builder;

@Builder
public record StepStatusCommand(
	Integer myStepCount,
	Integer opponentStepCount
) {
}