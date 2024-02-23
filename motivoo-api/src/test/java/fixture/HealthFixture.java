package fixture;

import java.util.Collections;
import java.util.List;

import lombok.val;
import sopt.org.motivoo.api.controller.health.dto.request.OnboardingRequest;
import sopt.org.motivoo.api.controller.health.dto.response.OnboardingResponse;

import sopt.org.motivoo.domain.health.entity.ExerciseFrequency;
import sopt.org.motivoo.domain.health.entity.ExerciseLevel;
import sopt.org.motivoo.domain.health.entity.ExerciseTime;
import sopt.org.motivoo.domain.health.entity.ExerciseType;
import sopt.org.motivoo.domain.health.entity.Health;
import sopt.org.motivoo.domain.health.entity.HealthNote;
import sopt.org.motivoo.domain.user.entity.User;

public class HealthFixture {
    private static final boolean IS_EXERCISE = true;
    private static final ExerciseType EXERCISE_TYPE = ExerciseType.HIGH_LEVEL_ACTIVE;
    private static final ExerciseFrequency EXERCISE_FREQUENCY = ExerciseFrequency.FIVE_OR_MORE_TIMES;
    private static final ExerciseTime EXERCISE_TIME = ExerciseTime.TWOHOURS_OR_MORE;
    private static final HealthNote HEALTH_NOTE = HealthNote.NECK;
    private static final ExerciseLevel EXERCISE_LEVEL = ExerciseLevel.ADVANCED;

    public static Health createHealthInfo(User user){
        // val user = UserFixture.createUserV4();
        val health = Health.builder()
                .user(user)
                .isExercise(IS_EXERCISE)
                .exerciseType(EXERCISE_TYPE)
                .exerciseFrequency(EXERCISE_FREQUENCY)
                .exerciseTime(EXERCISE_TIME)
                .healthNotes(Collections.singleton(HEALTH_NOTE))
                .exerciseLevel(EXERCISE_LEVEL)
                .build();
        return health;
    }

    public static OnboardingRequest createOnboardingRequest(){
        val user = UserFixture.createUser();
        user.updateParentchild(ParentchildFixture.createParentchild());
        val health = createHealthInfo(user);
        return new OnboardingRequest(user.getType().getValue(), 20, health.isExercise(), health.getExerciseType().getValue(),
            health.getExerciseFrequency().getValue(), health.getExerciseTime().getValue(), List.of("목"));
    }

    public static OnboardingResponse createOnboardingResponse(){
        val user = UserFixture.createUser();
        user.updateParentchild(ParentchildFixture.createParentchild());
        val health = HealthFixture.createHealthInfo(user);
        return new OnboardingResponse(1L, user.getParentchild().getInviteCode(), health.getExerciseLevel().getValue());
    }


}
