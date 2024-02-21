package sopt.org.motivoo.api.controller.healthcheck;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "헬스체크", description = "헬스체크용 Api")
public interface HealthCheckApi {

	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200"),
			@ApiResponse(responseCode = "400", content = @Content)
		}
	)
	@Operation(summary = "헬스체크")
	ResponseEntity<sopt.org.motivoo.common.response.ApiResponse<String>> healthCheck();
}
