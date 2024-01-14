package sopt.org.motivooServer.fixture;

import static sopt.org.motivooServer.fixture.UserFixture.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.val;
import sopt.org.motivooServer.domain.mission.dto.response.MissionHistoryResponse;
import sopt.org.motivooServer.domain.mission.dto.response.TodayMissionResponse;
import sopt.org.motivooServer.domain.mission.entity.CompletedStatus;
import sopt.org.motivooServer.domain.mission.entity.MissionQuest;
import sopt.org.motivooServer.domain.mission.entity.UserMission;
import sopt.org.motivooServer.domain.mission.entity.UserMissionChoices;
import sopt.org.motivooServer.domain.user.entity.User;

@RequiredArgsConstructor
public class UserMissionFixture {


	public static UserMission createUserMission() {
		val userMission = UserMission.builder()
			.user(createUser())
			.mission(MissionFixture.createMission())
			.missionQuest(createMissionQuest())
			.completedStatus(CompletedStatus.NONE).build();
		userMission.updateCreatedAt(LocalDateTime.now());
		userMission.updateImgUrl("s3 img url");
		return userMission;
	}

	public static UserMission createUserMissionV2() {
		val userMission = UserMission.builder()
			.user(createUser())
			.mission(MissionFixture.createMissionV2())
			.missionQuest(createMissionQuest())
			.completedStatus(CompletedStatus.NONE).build();
		userMission.updateCreatedAt(LocalDateTime.now());
		userMission.updateImgUrl("s3 img url");
		return userMission;
	}

	public static MissionQuest createMissionQuest() {
		return MissionQuest.builder()
			.content("손가락 하트 만들어서 찍기 ").build();
	}

	public static List<UserMission> createUserMissions() {
		List<UserMission> userMissions = new ArrayList<>();

		userMissions.add(createUserMission());
		userMissions.add(createUserMissionV2());
		return userMissions;
	}

	public static List<UserMissionChoices> createUserMissionChoices() {
		List<UserMissionChoices> userMissionChoices = new ArrayList<>();

		userMissionChoices.add(UserMissionChoices.builder()
			.user(createUser())
			.mission(MissionFixture.createMission()).build());
		userMissionChoices.add(UserMissionChoices.builder()
			.user(createUser())
			.mission(MissionFixture.createMissionV2()).build());
		return userMissionChoices;
	}

	public static MissionHistoryResponse createMissionHistoryResponse() {
		User childUser = createUser();
		User parentUser = createUserV2();

		return MissionHistoryResponse.of(childUser, createUserMission(), createUserMissions(), createUserMissions());
	}

	public static TodayMissionResponse createTodayMissionResponse() {
		return TodayMissionResponse.of(createUserMission());
	}
}
