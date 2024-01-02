package sopt.org.motivooServer.global.healthcheck;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.global.util.slack.SlackUtil;

@Slf4j
@AutoConfigureRestDocs
@WebMvcTest(value = HealthCheckController.class)
@WithMockUser(roles = "USER")
class HealthCheckControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SlackUtil slackUtil;

	@DisplayName("Health Check Controller 테스트")
	@Test
	void healthCheckControllerTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/health").header("Content-Type", "application/json"))
			.andDo(MockMvcResultHandlers.print())
			.andDo(MockMvcRestDocumentation.document("health-check-controller/health-check"))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
}