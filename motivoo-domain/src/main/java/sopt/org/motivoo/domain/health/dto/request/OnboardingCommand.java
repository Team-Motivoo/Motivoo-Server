package sopt.org.motivoo.domain.health.dto.request;

import java.util.List;

import lombok.Builder;

@Builder
public record OnboardingCommand(
        String type,

        int age,
        boolean isExercise,
        String exerciseType,
        String exerciseCount,
        String exerciseTime,
        List<String> exerciseNote
){
}
