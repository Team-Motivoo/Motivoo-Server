package sopt.org.motivooServer.domain.user.entity;

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
import jakarta.persistence.Table;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;
import sopt.org.motivooServer.domain.parentchild.Parentchild;

@Entity
@Table(name = "`user`")
@SQLDelete(sql = "UPDATE user SET user.deleted=true WHERE user_id=?")
public class User extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	private String username;

	private Integer age;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserType userType;

	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE;

	@ManyToOne
	@JoinColumn(name = "parentchild_id")
	private Parentchild parentchild;

	@Column(nullable = false)
	private Long socialId;

	private String socialNickname;

	private String socialAccessToken;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialPlatform socialPlatform;
}
