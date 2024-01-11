package sopt.org.motivooServer.domain.mission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class MissionQuest {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mission_quest_id")
	private Long id;

	@Column(nullable = false)
	private String content;
}
