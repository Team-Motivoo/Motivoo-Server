package sopt.org.motivoo.domain.parentchild.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.motivoo.domain.common.BaseTimeEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Parentchild extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "parentchild_id")
	private Long id;

	@Column(nullable = false)
	private boolean isMatched;

	@Column(nullable = false)
	private String inviteCode;

	@Builder
	private Parentchild(Boolean isMatched, String inviteCode){
		this.isMatched = isMatched;
		this.inviteCode = inviteCode;
	}

	public void matchingSuccess() {
		this.isMatched = true;
	}

}
