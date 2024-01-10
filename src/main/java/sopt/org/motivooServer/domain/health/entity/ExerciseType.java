package sopt.org.motivooServer.domain.health.entity;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.health.exception.HealthException;

import java.util.Arrays;

import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.INVALID_HEALTH_NOTE;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExerciseType {
    HIGH_LEVEL("고강도"),
    MEDIUM_LEVEL("중강도"),
    LOW_LEVEL("저강도")
    ;
    private final String value;

    public static ExerciseType of(String value) {
        return Arrays.stream(ExerciseType.values())
                .filter(exerciseType -> value.equals(exerciseType.value))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());//TO-DO
    }
}
