package sopt.org.motivoo.domain.health.entity;

import static sopt.org.motivoo.domain.health.exception.HealthExceptionType.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.motivoo.domain.common.BaseTimeEntity;
import sopt.org.motivoo.domain.user.entity.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Health extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "health_id")
	private Long id;

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@Column(nullable = false)
	private boolean isExercise;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExerciseType exerciseType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExerciseFrequency exerciseFrequency;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExerciseTime exerciseTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@ElementCollection
	private Set<HealthNote> healthNotes = new HashSet<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExerciseLevel exerciseLevel;

	@Builder
	private Health(User user, boolean isExercise, ExerciseType exerciseType,
				   ExerciseFrequency exerciseFrequency, ExerciseTime exerciseTime, Set<HealthNote> healthNotes, ExerciseLevel exerciseLevel){
		validateIsExercise();
		this.user = user;
		this.isExercise = isExercise;
		this.exerciseType = exerciseType;
		this.exerciseFrequency = exerciseFrequency;
		this.exerciseTime = exerciseTime;
		this.healthNotes = healthNotes;
		this.exerciseLevel = exerciseLevel;
	}

	private void validateIsExercise() {
		Objects.requireNonNull(isExercise, NULL_VALUE_IS_EXERCISE.message());
	}

	public void updateExerciseLevel(double score) {
		if(score>36 && score<=96)
			this.exerciseLevel = ExerciseLevel.ADVANCED;
		else if(score>=18 && score<=36)
			this.exerciseLevel = ExerciseLevel.INTERMEDIATE;
		else
			this.exerciseLevel = ExerciseLevel.BEGINNER;
	}

}
