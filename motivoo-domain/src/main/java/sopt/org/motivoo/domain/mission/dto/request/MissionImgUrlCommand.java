package sopt.org.motivoo.domain.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MissionImgUrlCommand(
	@NotBlank(message = "이미지 prefix는 필수 입력 값입니다.")
	@JsonProperty("img_prefix") String imgPrefix
) {
}
