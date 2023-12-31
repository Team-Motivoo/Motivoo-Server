package sopt.org.motivooServer.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static sopt.org.motivooServer.global.response.SuccessType.*;

import java.security.Principal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.auth.config.CustomJwtAuthenticationEntryPoint;
import sopt.org.motivooServer.domain.auth.config.JwtTokenProvider;
import sopt.org.motivooServer.domain.auth.config.RedisConfig;
import sopt.org.motivooServer.domain.auth.repository.TokenRedisRepository;
import sopt.org.motivooServer.global.healthcheck.HealthCheckController;
import sopt.org.motivooServer.global.response.ApiResponse;
import sopt.org.motivooServer.global.util.slack.SlackUtil;

@Slf4j
@DisplayName("HealthCheckController 테스트")
@WebMvcTest(HealthCheckController.class)
public class HealthCheckControllerTest extends BaseControllerTest {

	protected static final String DEFAULT_URL = "/api/health";

	@MockBean
	private SlackUtil slackUtil;

	@MockBean
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;

	@MockBean
	private HealthCheckController healthCheckController;

	@MockBean
	private RedisConfig redisConfig;

	@MockBean
	private TokenRedisRepository tokenRedisRepository;

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
				MockMvcRestDocumentation.document("healthCheck",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
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
						// .requestSchema(Schema.schema("FormParameter-HealthCheck"))
						// .responseSchema(Schema.schema("HealthCheckResponse.health"))
						.build()
				)
				)
			);

			resultActions.andExpect(MockMvcResultMatchers.status().isOk());
	}
}