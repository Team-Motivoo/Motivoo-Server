package sopt.org.motivoo.domain.mission.entity;


import static sopt.org.motivoo.domain.mission.entity.CompletedStatus.*;
import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import sopt.org.motivoo.domain.mission.exception.MissionException;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.entity.UserType;

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

	@Builder(builderMethodName = "builderForEmpty")
	private UserMission(CompletedStatus completedStatus, Mission mission, User user, MissionQuest missionQuest) {
		this.completedStatus = NONE;
		this.mission = mission;
		this.user = user;
		this.missionQuest = missionQuest;
	}

	@Builder(builderMethodName = "builderForDemo")
	private UserMission(CompletedStatus completedStatus, String imgUrl, Mission mission, MissionQuest missionQuest,
		User user) {
		this.completedStatus = completedStatus;
		this.imgUrl = imgUrl;
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

	public void updateCreatedAt(LocalDateTime date) {
		this.createdAt = date;
	}

	public void updateUpdatedAt(LocalDateTime date) {
		this.updatedAt = date;
	}

	public void updateImgUrl(final String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public void updateCompletedStatus(final CompletedStatus completedStatus) {
		this.completedStatus = completedStatus;
	}


	public void updateMissionFromEmpty(final Mission mission) {
		if (!isEmptyUserMission()) {
			throw new MissionException(ALREADY_CHOICE_TODAY_MISSION);
		}

		this.mission = mission;
		this.updateCompletedStatus(IN_PROGRESS);
	}

	public boolean isEmptyUserMission() {
		return this.getMission().getTarget().equals(UserType.NONE);
	}

	public boolean isNowDate() {
		return this.getCreatedAt().toLocalDate().equals(LocalDate.now());
	}
}
