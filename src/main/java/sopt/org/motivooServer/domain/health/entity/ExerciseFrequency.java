package sopt.org.motivooServer.domain.health.entity;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExerciseFrequency {

	// 1회 미만 | 1~2회 | 3~4회 | 5회 이상
    LESS_THAN_ONCE("1회 미만"),
    ONCE_OR_TWICE("1~2회"),
    THREE_OR_FOUR_TIMES("3~4회"),
    FIVE_OR_MORE_TIMES("5회 이상");

    private final String value;

    public static ExerciseFrequency of(String value) {
        return Arrays.stream(ExerciseFrequency.values())
                .filter(exerciseFrequency -> value.equals(exerciseFrequency.value))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());//TO-DO
    }
}
