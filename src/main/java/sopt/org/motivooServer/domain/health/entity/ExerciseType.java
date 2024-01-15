package sopt.org.motivooServer.domain.health.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.health.exception.HealthException;

import java.util.Arrays;

import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExerciseType {
    HIGH_LEVEL_ACTIVE("고강도 운동"),
    MEDIUM_LEVEL_ACTIVE("중강도 운동"),
    LOW_LEVEL_ACTIVE("저강도 운동"),
    HIGH_LEVEL_INACTIVE("고강도 활동"),
    MEDIUM_LEVEL_INACTIVE("중강도 활동"),
    LOW_LEVEL_INACTIVE("저강도 활동")
    ;
    private final String value;

    public static ExerciseType of(String value) {
        return Arrays.stream(ExerciseType.values())
                .filter(exerciseType -> value.equals(exerciseType.value))
                .findFirst()
                .orElseThrow(() -> new HealthException(INVALID_EXERCISE_TYPE));
    }
}
