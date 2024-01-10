package sopt.org.motivooServer.domain.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MissionImgUrlResponse(
	@JsonProperty("img_presigned_url") String imgPresignedUrl,
	@JsonProperty("file_name") String fileName
) {

	public static MissionImgUrlResponse of(String imgPresignedUrl, String fileName) {
		return new MissionImgUrlResponse(imgPresignedUrl, fileName);
	}
}
