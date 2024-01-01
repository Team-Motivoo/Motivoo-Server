package sopt.org.motivooServer.domain.parentchild;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;

@Entity
public class Parentchild extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "parentchild_id")
	private Long id;

	@Column(nullable = false)
	private Boolean isMatched;

	@Column(nullable = false)
	private String inviteCode;
}
