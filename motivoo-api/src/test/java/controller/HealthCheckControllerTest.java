package sopt.org.motivooServer.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static sopt.org.motivooServer.global.response.SuccessType.*;
import static sopt.org.motivooServer.util.ApiDocumentUtil.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.global.healthcheck.HealthCheckController;
import sopt.org.motivooServer.global.response.ApiResponse;

@Slf4j
@DisplayName("HealthCheckController 테스트")
@WebMvcTest(HealthCheckController.class)
public class HealthCheckControllerTest extends BaseControllerTest {

	protected static final String DEFAULT_URL = "/api/health";

	@MockBean
	private HealthCheckController healthCheckController;

	@DisplayName("Health Check Controller 테스트")
	@Test
	void healthCheckControllerTest() throws Exception {

		// given
		when(healthCheckController.healthCheck())
			.thenReturn(ApiResponse.success(HEALTH_CHECK_SUCCESS, "test success!"));

		// then
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.get(DEFAULT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andDo(
			document("healthCheck",
				getDocumentRequest(),
				getDocumentResponse(),
				resource(
					ResourceSnippetParameters.builder()
						.tag("HealthCheck API")
						.summary("헬스체크용 API")
						.description("헬스체크 성공여부 조회")
						.requestFields()
						.responseFields(
							fieldWithPath("code").type(NUMBER).description("상태 코드"),
							fieldWithPath("message").type(STRING).description("상태 메세지"),
							fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
							fieldWithPath("data").description("응답 데이터"))
						.build()
				)
			)
		);

		resultActions.andExpect(MockMvcResultMatchers.status().isOk());
	}
}