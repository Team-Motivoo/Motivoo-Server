package sopt.org.motivooServer.domain.mission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;

@Getter
@Entity
public class MissionQuest extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mission_quest_id")
	private Long id;

	@Column(nullable = false)
	private String content;
}