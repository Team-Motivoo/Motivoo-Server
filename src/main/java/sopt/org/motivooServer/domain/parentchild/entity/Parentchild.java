package sopt.org.motivooServer.domain.parentchild.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.common.BaseTimeEntity;

@Getter
@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Parentchild extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "parentchild_id")
	private Long id;

	@Column(nullable = false)
	private Boolean isMatched;

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
