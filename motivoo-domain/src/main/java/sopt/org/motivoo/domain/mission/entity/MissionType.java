package sopt.org.motivoo.domain.mission.entity;

import static sopt.org.motivoo.domain.mission.exception.MissionExceptionType.*;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.health.entity.ExerciseLevel;
import sopt.org.motivoo.domain.mission.exception.MissionException;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MissionType {

	ADVANCED("고수"),
	INTERMEDIATE("중수"),
	BEGINNER("초보"),
	BEGIN_INTER("초보&중수"),
	INTER_ADVANCE("중수&고수"),
	ALL("초보&중수&고수"),
	NONE("없음")

	;

	private final String value;


	// 유저의 운동 레벨이 미션 타입에 포함된다면 true를 반환
	public boolean containsLevel(ExerciseLevel level) {

		if (this.value.contains(level.getValue())) {
			return true;
		}
		return false;
		// return Arrays.stream(MissionType.values())
		// 	.anyMatch(missionType -> missionType.value.contains(level.getValue()));
	}

	public static MissionType of(String value) {
		return Arrays.stream(MissionType.values())
			.filter(missionType -> value.equals(missionType.value))
			.findFirst()
			.orElseThrow(() -> new MissionException(INVALID_MISSION_TYPE));
	}
}
