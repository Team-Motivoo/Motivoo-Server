package sopt.org.motivoo.api.controller.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.dto.response.ParentchildMissionDto;

@Builder
public record ParentchildMissionResponse(
	String date,
	@JsonProperty("my_mission_content") String myMissionContent,
	@JsonProperty("my_mission_img_url") String myMissionImgUrl,
	@JsonProperty("my_mission_status") String myMissionStatus,
	@JsonProperty("opponent_mission_content") String opponentMissionContent,
	@JsonProperty("opponent_mission_img_url") String opponentMissionImgUrl,
	@JsonProperty("opponent_mission_status") String opponentMissionStatus
) {

	public static ParentchildMissionResponse of(ParentchildMissionDto mission) {
		return ParentchildMissionResponse.builder()
			.date(mission.date())
			.myMissionContent(mission.myMissionContent())
			.myMissionImgUrl(mission.myMissionImgUrl())
			.myMissionStatus(mission.myMissionStatus())
			.opponentMissionContent(mission.opponentMissionContent())
			.opponentMissionImgUrl(mission.opponentMissionImgUrl())
			.opponentMissionStatus(mission.opponentMissionStatus()).build();
	}
}
