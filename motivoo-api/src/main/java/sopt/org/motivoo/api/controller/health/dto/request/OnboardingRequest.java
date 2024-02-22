package sopt.org.motivoo.api.controller.health.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.health.dto.request.OnboardingCommand;

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

        public OnboardingCommand toServiceDto() {
                return OnboardingCommand.builder()
                        .type(type)
                        .age(age)
                        .isExercise(isExercise)
                        .exerciseType(exerciseType)
                        .exerciseCount(exerciseCount)
                        .exerciseTime(exerciseTime)
                        .exerciseNote(exerciseNote).build();
        }
}
