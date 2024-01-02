package sopt.org.motivooServer.domain.health.entity;

import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.*;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.health.exception.HealthException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum HealthNote {

	WAIST("허리"),
	KNEE("무릎"),
	NECK("목"),
	WRIST("손목"),
	SHOULDER("어깨"),
	ANKLE("발목");

	private final String value;

	public static HealthNote of(String value) {
		return Arrays.stream(HealthNote.values())
			.filter(healthNote -> value.equals(healthNote.value))
			.findFirst()
			.orElseThrow(() -> new HealthException(INVALID_HEALTH_NOTE));
	}
}
