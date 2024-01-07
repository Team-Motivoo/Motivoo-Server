package sopt.org.motivooServer.global.healthcheck;

import static sopt.org.motivooServer.global.response.SuccessType.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.global.response.ApiResponse;
import sopt.org.motivooServer.global.util.s3.PreSignedUrlResponse;
import sopt.org.motivooServer.global.util.s3.S3BucketDirectory;
import sopt.org.motivooServer.global.util.s3.S3Service;

@Slf4j
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthCheckController implements HealthCheckApi {

	private final S3Service s3Service;

	@GetMapping
	public ResponseEntity<ApiResponse<String>> healthCheck() {
		return ApiResponse.success(HEALTH_CHECK_SUCCESS, "test success!");
	}

	// @ResponseStatus VS ResponseEntity
	@GetMapping("/v2")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<String> healthCheckV2() {
		return ApiResponse.successV2(HEALTH_CHECK_SUCCESS);
	}


	//== AWS S3 버킷 이미지 업로드 테스트 ==//

	// Multipart 요청 이용
	@PatchMapping("/image")
	public ApiResponse<String> uploadImage(@RequestPart MultipartFile image) throws IOException {
		String imgUrl = s3Service.uploadImage(S3BucketDirectory.TEST_PREFIX, image);
		log.info("S3 버킷에 이미지 업로드 성공!: {}", imgUrl);
		return ApiResponse.successV2(IMAGE_S3_UPLOAD_SUCCESS);
	}

	// PreSigned Url 이용 (클라이언트에서 해당 URL로 업로드)
	@PatchMapping("/image/pre")
	public ResponseEntity<ApiResponse<PreSignedUrlResponse>> uploadImageByPreSigned(@RequestParam String prefix) throws IOException {
		PreSignedUrlResponse preSignedUrl = s3Service.getUploadPreSignedUrl(S3BucketDirectory.of(prefix));
		log.info("S3 버킷에 업로드할 이미지 PreSigned URL 반환 성공!: url={}, filename={}", preSignedUrl.url(), preSignedUrl.fileName());
		return ApiResponse.success(IMAGE_S3_UPLOAD_SUCCESS, preSignedUrl);
	}

	// 버킷에서 이미지 삭제
	@DeleteMapping("/image")
	public ApiResponse<String> deleteImage(@RequestParam("img_url") String imageUrl) throws IOException {
		s3Service.deleteImage(imageUrl);
		log.info("S3 버킷에서 이미지 삭제 성공!");
		return ApiResponse.successV2(IMAGE_S3_DELETE_SUCCESS);
	}
}
