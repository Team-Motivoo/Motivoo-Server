package sopt.org.motivoo.api.controller.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.mission.dto.response.MissionImgUrlResult;

@Builder
public record MissionImgUrlResponse(
	@JsonProperty("img_presigned_url") String imgPresignedUrl,
	@JsonProperty("file_name") String fileName
) {

	public static MissionImgUrlResponse of(MissionImgUrlResult result) {
		return MissionImgUrlResponse.builder()
			.imgPresignedUrl(result.imgPresignedUrl())
			.fileName(result.fileName()).build();
	}
}
