package sopt.org.motivooServer.domain.health.entity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExerciseTime {

    LESS_THAN_HALFHOUR("30분 미만"),
    HALFHOUR_TO_ONEHOUR("30분~1시간"),
    ONEHOUR_TO_TWOHOURS("1시간~2시간"),
    TWOHOURS_OR_MORE("2시간 이상");

    private final String value;

}
