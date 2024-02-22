package sopt.org.motivoo.domain.health.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record OnboardingCommand(
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
