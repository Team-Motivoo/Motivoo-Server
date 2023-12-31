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
import sopt.org.motivooServer.domain.common.BaseTimeEntity;
import sopt.org.motivooServer.domain.user.entity.User;

@Entity
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
	private List<HealthNote> healthNotes = new ArrayList<>();
}
