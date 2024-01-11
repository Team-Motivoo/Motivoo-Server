package sopt.org.motivooServer.domain.health.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
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

	public static List<HealthNote> of(String values) {
		String[] strArr = values.split("\\s*,\\s*");
		return of(Arrays.asList(strArr));
	}
}
