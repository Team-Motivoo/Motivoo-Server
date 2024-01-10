package sopt.org.motivooServer.domain.health.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum HealthNote {

	WAIST("허리"),
	KNEE("무릎"),
	NECK("목"),
	WRIST("손목"),
	SHOULDER("어깨"),
	ANKLE("발목");

	private final String value;

	public static List<HealthNote> of(List<String> values) {
		return Arrays.stream(HealthNote.values())
				.filter(healthNote -> values.contains(healthNote.value))
				.collect(Collectors.toList());
	}
}
