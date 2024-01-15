package sopt.org.motivooServer.domain.health.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.health.exception.HealthException;

import java.util.Arrays;

import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.INVALID_EXERCISE_FREQUENCY;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExerciseFrequency {

    LESS_THAN_ONCE("1일 이하"),
    ONCE_OR_TWICE("1일 - 3일 이내"),
    THREE_OR_FOUR_TIMES("3일 - 5일 이내"),
    FIVE_OR_MORE_TIMES("5일 - 매일");

    private final String value;

    public static ExerciseFrequency of(String value) {
        return Arrays.stream(ExerciseFrequency.values())
                .filter(exerciseFrequency -> value.equals(exerciseFrequency.value))
                .findFirst()
                .orElseThrow(() -> new HealthException(INVALID_EXERCISE_FREQUENCY));
    }
}
