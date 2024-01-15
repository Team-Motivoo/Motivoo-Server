package sopt.org.motivooServer.domain.mission.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;
import sopt.org.motivooServer.domain.user.entity.User;

@Getter
@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMissionChoices extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_mission_choices_id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "mission_id", nullable = false)
	private Mission mission;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@Builder
	public UserMissionChoices(Mission mission, User user) {
		this.mission = mission;
		this.user = user;
	}
}
