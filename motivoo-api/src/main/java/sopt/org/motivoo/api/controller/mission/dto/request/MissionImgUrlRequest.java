package sopt.org.motivoo.api.controller.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import sopt.org.motivoo.domain.mission.dto.request.MissionImgUrlCommand;

public record MissionImgUrlRequest(
	@NotBlank(message = "이미지 prefix는 필수 입력 값입니다.")
	@JsonProperty("img_prefix") String imgPrefix
) {

	public MissionImgUrlCommand toServiceDto() {
		return MissionImgUrlCommand.builder()
			.imgPrefix(imgPrefix).build();
	}
}
