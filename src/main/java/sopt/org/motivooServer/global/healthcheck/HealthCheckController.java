package sopt.org.motivooServer.global.healthcheck;

import static sopt.org.motivooServer.global.response.SuccessType.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.global.response.ApiResponse;
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

	@PatchMapping("/image")
	public ApiResponse<String> uploadImage(@RequestPart MultipartFile image) throws IOException {
		String imgUrl = s3Service.uploadImage("test/", image);
		log.info("S3 버킷에 이미지 업로드 성공!: {}", imgUrl);
		return ApiResponse.successV2(IMAGE_S3_UPLOAD_SUCCESS);
	}

	@DeleteMapping("/image")
	public ApiResponse<String> deleteImage(@RequestParam("img_url") String imageUrl) throws IOException {
		s3Service.deleteImage(imageUrl);
		log.info("S3 버킷에 이미지 삭제 성공!");
		return ApiResponse.successV2(IMAGE_S3_DELETE_SUCCESS);
	}
}
