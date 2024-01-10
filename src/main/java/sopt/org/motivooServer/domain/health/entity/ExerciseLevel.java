package sopt.org.motivooServer.domain.health.entity;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

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
                .orElseThrow(() -> new EntityNotFoundException());//TO-DO
    }
}
