package sopt.org.motivooServer.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;
import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;

@Getter
@Entity
@Table(name = "`user`")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE user SET user.deleted=true WHERE user_id=?")
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
	private Boolean deleted = Boolean.FALSE;

	@Column(nullable = false)
	private String socialId;

	private String nickname;

	private String socialAccessToken;

	private String refreshToken;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialPlatform socialPlatform;

	@ManyToOne
	@JoinColumn(name = "parentchild_id")
	private Parentchild parentchild;

	@OneToMany(mappedBy = "user")
	private List<UserMission> userMissions = new ArrayList<>();

	@Builder
	private User(String nickname, String socialId, SocialPlatform socialPlatform, String socialAccessToken,
				 String refreshToken, UserType type, boolean deleted) {
		this.nickname = nickname;
		this.socialId = socialId;
		this.socialPlatform = socialPlatform;
		this.socialAccessToken = socialAccessToken;
		this.refreshToken = refreshToken;
		this.type = type;
		this.deleted = deleted;
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

	public void updateOnboardingInfo(UserType type, Integer age) {
		this.type = type;
		this.age = age;
	}

}