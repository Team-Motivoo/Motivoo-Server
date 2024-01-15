package sopt.org.motivooServer.domain.mission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;
import sopt.org.motivooServer.domain.user.entity.UserType;

@Getter
@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mission_id")
	private Long id;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private String healthNotes;

	@Column(nullable = false)
	private int stepCount;

	@Column(columnDefinition = "TEXT")
	private String descriptionUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserType target;

	@Column(nullable = false)
	private String bodyPart;

	@Column(columnDefinition = "TEXT")
	private String iconUrl;

	@Builder
	private Mission(String content, String type, String healthNotes, int stepCount, String descriptionUrl,
		UserType target,
		String bodyPart, String iconUrl) {
		this.content = content;
		this.type = type;
		this.healthNotes = healthNotes;
		this.stepCount = stepCount;
		this.descriptionUrl = descriptionUrl;
		this.target = target;
		this.bodyPart = bodyPart;
		this.iconUrl = iconUrl;
	}
}
