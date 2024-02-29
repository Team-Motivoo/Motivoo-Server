package sopt.org.motivoo.external.s3;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PreSignedUrlResponse(
	@JsonProperty("file_name") String fileName,
	String url
) {

	public static PreSignedUrlResponse of(String fileName, String url) {
		return new PreSignedUrlResponse(fileName, url);
	}
}
