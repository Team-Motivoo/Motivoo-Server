package sopt.org.motivooServer.domain.health.service;

import org.springframework.stereotype.Service;
import sopt.org.motivooServer.domain.health.entity.ExerciseFrequency;
import sopt.org.motivooServer.domain.health.entity.ExerciseTime;
import sopt.org.motivooServer.domain.health.entity.ExerciseType;

@Service
public class CalculateScore {
    public double calculate(boolean isExercise, ExerciseType type,
                            ExerciseFrequency frequency, ExerciseTime time){
        double exerciseScore = getExerciseScore(isExercise, type);
        double frequencyScore = getFrequencyScore(frequency);
        double timeScore = getTimeScore(time);

        return exerciseScore*frequencyScore*timeScore;
    }
    private double getExerciseScore(boolean isExercise, ExerciseType type) {
        if(!isExercise){
            return switch (type) {
                case HIGH_LEVEL_ACTIVE, HIGH_LEVEL_INACTIVE -> 4.2;
                case MEDIUM_LEVEL_ACTIVE, MEDIUM_LEVEL_INACTIVE -> 2.1;
                default -> 1.05;
            };
        }
        return switch (type) {
            case HIGH_LEVEL_ACTIVE, HIGH_LEVEL_INACTIVE -> 6;
            case MEDIUM_LEVEL_ACTIVE, MEDIUM_LEVEL_INACTIVE -> 3;
            default -> 1.5;
        };
    }
    private double getFrequencyScore(ExerciseFrequency frequency) {
        return switch (frequency) {
            case LESS_THAN_ONCE -> 1.0;
            case ONCE_OR_TWICE -> 2.0;
            case THREE_OR_FOUR_TIMES -> 3.0;
            default -> 4.0;
        };
    }
    private double getTimeScore(ExerciseTime time) {
        return switch (time) {
            case LESS_THAN_HALFHOUR -> 1.0;
            case HALFHOUR_TO_ONEHOUR -> 2.0;
            case ONEHOUR_TO_TWOHOURS -> 3.0;
            default -> 4.0;
        };
    }

}
