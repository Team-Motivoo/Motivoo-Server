package sopt.org.motivooServer.domain.step.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;
import sopt.org.motivooServer.domain.user.entity.User;

@Entity
public class Step extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "step_id")
	private Long id;

	@Column(nullable = false)
	private Integer count;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
}
