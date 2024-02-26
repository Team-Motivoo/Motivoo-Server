package sopt.org.motivooServer.domain.health.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.health.exception.HealthException;

import java.util.Arrays;

import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.INVALID_EXERCISE_LEVEL;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExerciseLevel {
    ADVANCED("고수"),
    INTERMEDIATE("중수"),
    BEGINNER("초보");
    private final String value;

    public static ExerciseLevel of(String value) {
        return Arrays.stream(ExerciseLevel.values())
                .filter(exerciseLevel -> value.equals(exerciseLevel.value))
                .findFirst()
                .orElseThrow(() -> new HealthException(INVALID_EXERCISE_LEVEL));
    }
}
