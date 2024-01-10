package sopt.org.motivooServer.domain.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public record MissionImgUrlRequest(
	@NotBlank(message = "이미지 prefix는 필수 입력 값입니다.")
	@JsonProperty("img_prefix") String imgPrefix
) {
}
