package sopt.org.motivoo.domain.mission.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	public void setCreatedAtNow(LocalDateTime dateTime) {
		this.createdAt = dateTime;
	}

	public void setUser(User user) {
		this.user = user;

		if (!user.getUserMissionChoice().contains(this)) {
			user.getUserMissionChoice().add(this);
		}
	}

	public void deleteUser() {
		this.user = null;
	}
}
