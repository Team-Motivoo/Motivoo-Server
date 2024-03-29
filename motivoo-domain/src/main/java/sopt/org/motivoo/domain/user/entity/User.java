package sopt.org.motivoo.domain.user.entity;


import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.common.BaseTimeEntity;
import sopt.org.motivoo.domain.mission.entity.UserMission;
import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;
import sopt.org.motivoo.domain.mission.exception.MissionException;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;

@Slf4j
@Getter
@Entity
@Table(name = "`user`")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	private Integer age;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserType type;

	@Column(nullable = false)
	private boolean deleted = Boolean.FALSE;

	@Column(name = "delete_expired") //TODO 필요없는 필드! 추후 삭제
	private boolean deleteExpired = Boolean.FALSE;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(nullable = false)
	private String socialId;

	private String nickname;

	// private String socialAccessToken;

	private String refreshToken;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialPlatform socialPlatform;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "parentchild_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Parentchild parentchild;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private final List<UserMission> userMissions = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private final List<UserMissionChoices> userMissionChoice = new ArrayList<>();

	@Builder
	private User(String nickname, String socialId, SocialPlatform socialPlatform,
				 String refreshToken, UserType type, boolean deleted) {
		this.nickname = nickname;
		this.socialId = socialId;
		this.socialPlatform = socialPlatform;
		this.refreshToken = refreshToken;
		this.type = type;
		this.deleted = deleted;
	}

	@Builder(builderMethodName = "builderInTest")
	private User(Integer age, UserType type, String socialId, String nickname, SocialPlatform socialPlatform) {
		this.age = age;
		this.type = type;
		this.socialId = socialId;
		this.nickname = nickname;
		this.socialPlatform = socialPlatform;
	}


	//== 연관관계 메서드 ==//
	public void addUserMission(UserMission userMission) {
		this.userMissions.add(userMission);
		if (userMission.getUser() != this) {
			userMission.setUser(this);
		}
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void updateDeleted(){
		this.deleted = true;
	}

	public void updateDeleteAt(){
		this.deletedAt = LocalDateTime.now().plusDays(30);
	}

	public void updateOnboardingInfo(UserType type, Integer age) {
		this.type = type;
		this.age = age;
	}

	public void deleteSocialInfo() {
		this.socialPlatform = SocialPlatform.WITHDRAW;
	}

	public void updateParentchild(Parentchild parentchild) {
		this.parentchild = parentchild;
	}

	public void setParentchildToNull(){
		this.parentchild = null;
	}
	public void setUserMissionToNull(){
		this.userMissions.clear();
	}

	public void setUserMissionChoiceToNull(){
		this.userMissionChoice.clear();
	}

	public void addTodayUserMissionChoice(List<UserMissionChoices> userMissionChoice) {
		log.info("UserMission 선택지(매일 자정 초기화 후, 메인 홈 첫 진입 시 업데이트: {}가지 / User-{}가지", userMissionChoice.size(), this.userMissionChoice.size());
		this.userMissionChoice.addAll(userMissionChoice);
		userMissionChoice.forEach(umc -> {
			if (umc.getUser() != this) {
				umc.setUser(this);
			}
		});
	}

	// 가장 최근의 운동 미션 조회
	public UserMission getCurrentUserMission() {
		if (!userMissions.isEmpty()) {
			int lastIndex = userMissions.size() - 1;
			return userMissions.get(lastIndex);
		}

		throw new MissionException(EMPTY_USER_MISSIONS);
	}
}