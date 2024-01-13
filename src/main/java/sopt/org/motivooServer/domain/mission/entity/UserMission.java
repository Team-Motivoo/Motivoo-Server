package sopt.org.motivooServer.domain.mission.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;
import sopt.org.motivooServer.domain.user.entity.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserMission extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_mission_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CompletedStatus completedStatus;

	@Column(columnDefinition = "TEXT")
	private String imgUrl;

	@OneToOne
	@JoinColumn(name = "mission_id", nullable = false)
	private Mission mission;

	@OneToOne
	private MissionQuest missionQuest;

	@ManyToOne
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@Builder
	private UserMission(CompletedStatus completedStatus, Mission mission, MissionQuest missionQuest, User user) {
		this.completedStatus = completedStatus;
		this.mission = mission;
		this.missionQuest = missionQuest;
		this.user = user;
	}

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

	public void updateCompletedStatus(final CompletedStatus completedStatus) {
		this.completedStatus = completedStatus;
	}

}
