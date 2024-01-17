package sopt.org.motivooServer.domain.health.dto.request;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import sopt.org.motivooServer.domain.health.entity.Health;
import sopt.org.motivooServer.domain.health.entity.HealthNote;
import sopt.org.motivooServer.domain.user.entity.User;

@Builder
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
        public static OnboardingRequest of(User user, Health health) {
                List<String> stringNotes = health.getHealthNotes().stream()
                        .map(HealthNote::getValue)
                        .collect(Collectors.toList());
                return OnboardingRequest.builder()
                        .age(user.getAge())
                        .isExercise(health.isExercise())
                        .exerciseType(String.valueOf(health.getExerciseType()))
                        .exerciseCount(String.valueOf(health.getExerciseTime()))
                        .exerciseTime(String.valueOf(health.getExerciseTime()))
                        .exerciseNote(stringNotes)
                        .build();
        }
}
