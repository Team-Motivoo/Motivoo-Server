package sopt.org.motivooServer.domain.health.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.health.exception.HealthException;

import java.util.Arrays;

import static sopt.org.motivooServer.domain.health.exception.HealthExceptionType.INVALID_EXERCISE_TIME;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExerciseTime {

    LESS_THAN_HALFHOUR("30분 미만"),
    HALFHOUR_TO_ONEHOUR("30분 - 1시간"),
    ONEHOUR_TO_TWOHOURS("1시간 - 2시간"),
    TWOHOURS_OR_MORE("2시간 이상");

    private final String value;

    public static ExerciseTime of(String value) {
        return Arrays.stream(ExerciseTime.values())
                .filter(exerciseTime -> value.equals(exerciseTime.value))
                .findFirst()
                .orElseThrow(() -> new HealthException(INVALID_EXERCISE_TIME));
    }

}
