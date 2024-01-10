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

        return exerciseScore+frequencyScore+timeScore;
    }
    private double getExerciseScore(boolean isExercise, ExerciseType type) {
        if(!isExercise){
            switch (type) {
                case HIGH_LEVEL:
                    return 4.2;
                case MEDIUM_LEVEL:
                    return 2.1;
                default:
                    return 1.05;
            }
        }
        switch (type) {
            case HIGH_LEVEL:
                return 6;
            case MEDIUM_LEVEL:
                return 3;
            default:
                return 1.5;
        }
    }
    private double getFrequencyScore(ExerciseFrequency frequency) {
        switch (frequency) {
            case LESS_THAN_ONCE:
                return 1.0;
            case ONCE_OR_TWICE:
                return 2.0;
            case THREE_OR_FOUR_TIMES:
                return 3.0;
            default:
                return 4.0;
        }
    }
    private double getTimeScore(ExerciseTime time) {
        switch (time) {
            case LESS_THAN_HALFHOUR:
                return 1.0;
            case HALFHOUR_TO_ONEHOUR:
                return 2.0;
            case ONEHOUR_TO_TWOHOURS:
                return 3.0;
            default:
                return 4.0;
        }
    }

}
