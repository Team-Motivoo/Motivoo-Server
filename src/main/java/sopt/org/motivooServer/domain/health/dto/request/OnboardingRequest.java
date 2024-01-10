package sopt.org.motivooServer.domain.health.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import sopt.org.motivooServer.domain.health.entity.ExerciseFrequency;
import sopt.org.motivooServer.domain.health.entity.ExerciseTime;
import sopt.org.motivooServer.domain.health.entity.ExerciseType;
import sopt.org.motivooServer.domain.health.entity.HealthNote;
import sopt.org.motivooServer.domain.user.entity.UserType;

import java.util.List;

public record OnboardingRequest (
        String type,
        int age,
        @JsonProperty("is_exercise")
        boolean isExercise,
        @JsonProperty("exercise_type")
        String exerciseType,
        @JsonProperty("exercise_count")
        String exerciseCount,
        @JsonProperty("exercise_time")
        String exerciseTime,
        @JsonProperty("exercise_note")
        List<String> exerciseNote
){

}
