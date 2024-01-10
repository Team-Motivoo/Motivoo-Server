package sopt.org.motivooServer.domain.mission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;
import sopt.org.motivooServer.domain.user.entity.User;

@Getter
@Entity
public class UserMission extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_mission_id")
	private Long id;

	@Column(nullable = false)
	private Double completedRate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CompletedStatus completedStatus;

	@Column(columnDefinition = "TEXT")
	private String imgUrl;

	@OneToOne
	@JoinColumn(name = "mission_id", nullable = false)
	private Mission mission;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	//== 연관관계 메서드 ==//
	public void setUser(User user) {
		this.user = user;

		if (!user.getUserMissions().contains(this)) {
			user.getUserMissions().add(this);
		}
	}

	public void updateImgUrl(final String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
