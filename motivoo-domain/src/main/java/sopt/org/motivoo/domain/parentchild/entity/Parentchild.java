package sopt.org.motivoo.domain.parentchild.entity;

import static sopt.org.motivoo.domain.parentchild.exception.ParentchildExceptionType.*;

import java.util.List;

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
import sopt.org.motivoo.domain.parentchild.exception.ParentchildException;
import sopt.org.motivoo.domain.parentchild.exception.ParentchildExceptionType;
import sopt.org.motivoo.domain.user.entity.User;

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

	// TODO isMatched와 불일치 여부 테스트 후 하나만 남기기
	public boolean validateParentchild(int userCnt) {

		// 부모자식 관계에 대한 예외처리
		if (userCnt >= 3) {
			throw new ParentchildException(INVALID_PARENTCHILD_RELATION);
		}
		return userCnt == 2 && isMatched;
	}
}
