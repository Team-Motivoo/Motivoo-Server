package sopt.org.motivooServer.domain.health.entity;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExerciseTime {

    LESS_THAN_HALFHOUR("30분 미만"),
    HALFHOUR_TO_ONEHOUR("30분~1시간"),
    ONEHOUR_TO_TWOHOURS("1시간~2시간"),
    TWOHOURS_OR_MORE("2시간 이상");

    private final String value;

    public static ExerciseTime of(String value) {
        return Arrays.stream(ExerciseTime.values())
                .filter(exerciseTime -> value.equals(exerciseTime.value))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());//TO-DO
    }

}
