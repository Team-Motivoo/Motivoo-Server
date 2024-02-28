package sopt.org.motivoo.external.s3;

public record PreSignedUrlResponse(
	String fileName,
	String url
) {

	public static PreSignedUrlResponse of(String fileName, String url) {
		return new PreSignedUrlResponse(fileName, url);
	}
}
