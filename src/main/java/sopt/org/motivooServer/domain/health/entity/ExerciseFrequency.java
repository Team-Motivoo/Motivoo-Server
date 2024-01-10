package sopt.org.motivooServer.domain.health.entity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExerciseFrequency {

	// 1회 미만 | 1~2회 | 3~4회 | 5회 이상
    LESS_THAN_ONCE("1회 미만"),
    ONCE_OR_TWICE("1~2회"),
    THREE_OR_FOUR_TIMES("3~4회"),
    FIVE_OR_MORE_TIMES("5회 이상");

    private final String value;


}
