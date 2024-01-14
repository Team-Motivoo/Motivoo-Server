package sopt.org.motivooServer.fixture;

import lombok.val;
import sopt.org.motivooServer.domain.mission.entity.Mission;
import sopt.org.motivooServer.domain.user.entity.UserType;

public class MissionFixture {

	private static final String MISSION_CONTENT = "10000걸음 걷고 벽스쿼트 45초 하기";
	private static final int MISSION_STEP_COUNT = 10000;
	private static final String MISSION_BODY_PART = "하체";
	private static final String MISSION_ICON_URL = "https://gayeong04.notion.site/20a76151a5804d44b00086c60f0376c2?pvs=4";
	private static final String MISSION_HEALTH_NOTES = "무릎";
	private static final UserType MISSION_TARGET = UserType.CHILD;
	private static final String MISSION_TYPE = "고수";

	private static final String MISSION_CONTENT2 = "10000걸음 걷고 벽스쿼트 45초 하기";
	private static final int MISSION_STEP_COUNT2 = 10000;
	private static final String MISSION_BODY_PART2 = "하체";
	private static final String MISSION_ICON_URL2 = "https://gayeong04.notion.site/20a76151a5804d44b00086c60f0376c2?pvs=4";
	private static final String MISSION_HEALTH_NOTES2 = "무릎";
	private static final UserType MISSION_TARGET2 = UserType.CHILD;
	private static final String MISSION_TYPE2 = "고수";


	public static Mission createMission() {
		val mission = Mission.builder()
			.bodyPart(MISSION_BODY_PART)
			.content(MISSION_CONTENT)
			.stepCount(MISSION_STEP_COUNT)
			.iconUrl(MISSION_ICON_URL)
			.healthNotes(MISSION_HEALTH_NOTES)
			.target(MISSION_TARGET)
			.type(MISSION_TYPE).build();
		return mission;
	}

	public static Mission createMissionV2() {
		val mission = Mission.builder()
			.bodyPart(MISSION_BODY_PART2)
			.content(MISSION_CONTENT2)
			.stepCount(MISSION_STEP_COUNT2)
			.iconUrl(MISSION_ICON_URL2)
			.healthNotes(MISSION_HEALTH_NOTES2)
			.target(MISSION_TARGET2)
			.type(MISSION_TYPE2).build();
		return mission;
	}
}
