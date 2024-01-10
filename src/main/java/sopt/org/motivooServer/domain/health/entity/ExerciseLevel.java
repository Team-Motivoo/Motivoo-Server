package sopt.org.motivooServer.domain.health.entity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExerciseLevel {
    ADVANCED("고수"),
    INTERMEDIATE("중수"),
    BEGINNER("초보");
    private final String value;
}
