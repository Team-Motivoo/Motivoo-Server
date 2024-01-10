package sopt.org.motivooServer.domain.health.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;
import sopt.org.motivooServer.domain.user.entity.User;

@Getter
@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Health extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "health_id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private Boolean isExercise;

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
	private final List<HealthNote> healthNotes = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExerciseLevel exerciseLevel;

	@Builder
	private Health(User user, boolean isExercise, ExerciseType exerciseType,
				   ExerciseFrequency exerciseFrequency, ExerciseTime exerciseTime, List<HealthNote> healthNotes, ExerciseLevel exerciseLevel){
		this.user = user;
		this.isExercise = isExercise;
		this.exerciseType = exerciseType;
		this.exerciseFrequency = exerciseFrequency;
		this.exerciseTime = exerciseTime;
		this.healthNotes = healthNotes;
		this.exerciseLevel = exerciseLevel;
	}

	public void updateExerciseLevel(double score){
		if(score>36 && score<=96)
			this.exerciseLevel = ExerciseLevel.ADVANCED;
		else if(score>=18 && score<=36)
			this.exerciseLevel = ExerciseLevel.INTERMEDIATE;
		else
			this.exerciseLevel = ExerciseLevel.BEGINNER;
	}
}
