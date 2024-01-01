package sopt.org.motivooServer.domain.health;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "헬스체크", description = "헬스체크용 Api")
public interface HealthApi {

	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200"),
			@ApiResponse(responseCode = "400", content = @Content)
		}
	)
	@Operation(summary = "헬스체크")
	sopt.org.motivooServer.global.common.response.ApiResponse<String> healthCheck();
}
