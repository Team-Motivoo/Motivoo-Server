package sopt.org.motivooServer.global.util.s3;

import static sopt.org.motivooServer.global.advice.CommonExceptionType.*;
import static sopt.org.motivooServer.global.util.s3.S3ExceptionType.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import sopt.org.motivooServer.global.advice.BusinessException;
import sopt.org.motivooServer.global.config.aws.AWSConfig;

@Slf4j
@Component
public class S3Service {

	private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp");
	private static final Long MAX_FILE_SIZE = 5 * 1024 * 1024L;

	private static final Long PRE_SIGNED_URL_EXPIRE_MINUTE = 1L;  // 만료시간 1분
	private static final String IMAGE_EXTENSION = ".jpg";

	private final String bucketName;

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	public S3Service(@Value("${aws-property.s3-bucket-name}") final String bucketName, final S3Client s3Client, final S3Presigner s3Presigner) {
		this.bucketName = bucketName;
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
	}

	// PreSigned Url을 통한 이미지 업로드
	public PreSignedUrlResponse getUploadPreSignedUrl(final S3BucketDirectory prefix) {
		final String fileName = generateImageFileName();   // UUID 문자열
		final String key = prefix.value() + fileName;

		log.info("S3 세팅 성공!: {}", key);
		log.info("업로드할 image 경로: {}", prefix);

		try {
			PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key).build();

			PutObjectPresignRequest preSignedUrlRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(PRE_SIGNED_URL_EXPIRE_MINUTE))
				.putObjectRequest(request).build();

			String url = s3Presigner.presignPutObject(preSignedUrlRequest).url().toString();
			return PreSignedUrlResponse.of(fileName, url);
		} catch (RuntimeException e) {
			throw new BusinessException(FAIL_TO_GET_IMAGE_PRE_SIGNED_URL);
		}
	}

	// Multipart 요청을 통한 이미지 업로드
	public String uploadImage(final S3BucketDirectory directoryPath, MultipartFile image) throws IOException {
		validateExtension(image);
		validateFileSize(image);

		final String key = directoryPath.value() + generateImageFileName();
		log.info("S3 세팅 성공!: {}", key);
		log.info("업로드할 image: {}", image);

		try {
			PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(image.getContentType())
				.contentDisposition("inline").build();

			RequestBody requestBody = RequestBody.fromBytes(image.getBytes());
			s3Client.putObject(request, requestBody);
			return key;
		} catch (RuntimeException e) {
			throw new BusinessException(FAIL_TO_UPLOAD_IMAGE);
		}
	}


	// S3 버킷에 업로드된 이미지 삭제
	public void deleteImage(String key) {
		try {
			s3Client.deleteObject((DeleteObjectRequest.Builder builder) ->
				builder.bucket(bucketName)
					.key(key).build());
		} catch (RuntimeException e) {
			throw new BusinessException(FAIL_TO_DELETE_IMAGE);
		}
	}

	public String getImgByFileName(String prefix, String fileName) {
		return "https://"+bucketName+".s3.ap-northeast-2.amazonaws.com/"+prefix+fileName;
	}

	private String generateImageFileName() {
		return UUID.randomUUID() + IMAGE_EXTENSION;
	}

	private void validateExtension(MultipartFile image) {
		String contentType = image.getContentType();
		if (!IMAGE_EXTENSIONS.contains(contentType)) {
			throw new BusinessException(UNSUPPORTED_IMAGE_EXTENSION);
		}
	}

	private void validateFileSize(MultipartFile image) {
		if (image.getSize() > MAX_FILE_SIZE) {
			throw new BusinessException(UNSUPPORTED_IMAGE_SIZE);
		}
	}
}
