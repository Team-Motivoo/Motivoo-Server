package sopt.org.motivooServer.fixture;

import lombok.val;
import sopt.org.motivooServer.domain.health.entity.*;

import java.util.Collections;

public class HealthFixture {
    private static final boolean IS_EXERCISE = true;
    private static final ExerciseType EXERCISE_TYPE = ExerciseType.HIGH_LEVEL_ACTIVE;
    private static final ExerciseFrequency EXERCISE_FREQUENCY = ExerciseFrequency.FIVE_OR_MORE_TIMES;
    private static final ExerciseTime EXERCISE_TIME = ExerciseTime.TWOHOURS_OR_MORE;
    private static final HealthNote HEALTH_NOTE = HealthNote.NECK;
    private static final ExerciseLevel EXERCISE_LEVEL = ExerciseLevel.ADVANCED;

    public static Health createHealthInfo(){
        val user = UserFixture.createUserV4();
        val health = Health.builder()
                .user(user)
                .isExercise(IS_EXERCISE)
                .exerciseType(EXERCISE_TYPE)
                .exerciseFrequency(EXERCISE_FREQUENCY)
                .exerciseTime(EXERCISE_TIME)
                .healthNotes(Collections.singletonList(HEALTH_NOTE))
                .exerciseLevel(EXERCISE_LEVEL)
                .build();
        return health;
    }


}
