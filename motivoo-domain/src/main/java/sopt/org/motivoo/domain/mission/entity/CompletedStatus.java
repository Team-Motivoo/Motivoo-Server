package sopt.org.motivoo.domain.mission.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CompletedStatus {

	SUCCESS("성공"),
	FAIL("실패"),
	IN_PROGRESS("진행중"),
	NONE("없음");

	private final String value;
}
