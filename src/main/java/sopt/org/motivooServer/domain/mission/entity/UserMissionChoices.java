package sopt.org.motivooServer.domain.mission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public UserMissionChoices(Mission mission, User user) {
		this.mission = mission;
		this.user = user;
	}
}
