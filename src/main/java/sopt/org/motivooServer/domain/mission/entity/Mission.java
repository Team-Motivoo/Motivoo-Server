package sopt.org.motivooServer.domain.mission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;
import sopt.org.motivooServer.domain.user.entity.UserType;

@Entity
public class Mission extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mission_id")
	private Long id;

	@Column(nullable = false)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MissionType type;

	@Column(nullable = false)
	private String healthNotes;

	@Column(nullable = false)
	private int stepCount;

	@Column(columnDefinition = "TEXT")
	private String descriptionUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserType target;

}
