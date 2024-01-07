package sopt.org.motivooServer.domain.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;


public record MissionImgUrlRequest(
	@JsonProperty("img_prefix") String imgPrefix
) {
}
