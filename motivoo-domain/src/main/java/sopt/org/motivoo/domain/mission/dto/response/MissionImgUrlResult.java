package sopt.org.motivoo.domain.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MissionImgUrlResult(
	@JsonProperty("img_presigned_url") String imgPresignedUrl,
	@JsonProperty("file_name") String fileName
) {

	public static MissionImgUrlResult of(String imgPresignedUrl, String fileName) {
		return new MissionImgUrlResult(imgPresignedUrl, fileName);
	}
}
