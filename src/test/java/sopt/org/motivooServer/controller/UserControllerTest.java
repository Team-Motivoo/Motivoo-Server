package sopt.org.motivooServer.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sopt.org.motivooServer.global.response.SuccessType.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.user.controller.UserController;
import sopt.org.motivooServer.domain.user.dto.response.MyPageInfoResponse;
import sopt.org.motivooServer.global.healthcheck.HealthCheckController;
import sopt.org.motivooServer.global.response.ApiResponse;
import sopt.org.motivooServer.global.util.slack.SlackUtil;

@Slf4j
@WithMockUser(roles = "USER")
@DisplayName("UserController 테스트")
@WebMvcTest(UserController.class)
public class UserControllerTest extends BaseControllerTest {

	protected static final String DEFAULT_URL = "/mypage";
	private final String TAG = "마이페이지";

	@MockBean
	private SlackUtil slackUtil;

	@MockBean
	UserController userController;

	@Test
	@DisplayName("마이페이지 조회 테스트")
	void userControllerMyPageTest() throws Exception {

		// given
		Long userId = 1L;
		MyPageInfoResponse response = MyPageInfoResponse.builder()
				.userNickname("모티뿡뿡이")
				.userAge(20)
				.userType("CHILD").build();
		ResponseEntity<ApiResponse<MyPageInfoResponse>> result = ApiResponse
			.success(GET_MYPAGE_INFO_SUCCESS, response);

		// when
		when(userController.getMyPage(userId)).thenReturn(result);

		// then
		mockMvc.perform(get(DEFAULT_URL + "/info/{userId}", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andDo(
			document("마이페이지 조회 API 성공 Example",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				resource(
					ResourceSnippetParameters.builder()
						.tag(TAG)
						.summary("헬스체크용 API")
						.description("마이페이지 홈 조회")
						.requestFields()
						.pathParameters(
							parameterWithName("userId")
						)
						.responseFields(
							fieldWithPath("code").type(NUMBER).description("상태 코드"),
							fieldWithPath("message").type(STRING).description("상태 메세지"),
							fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
							fieldWithPath("data").type(OBJECT).description("응답 데이터"),
							fieldWithPath("data.user_nickname").type(STRING).description("응답 데이터"),
							fieldWithPath("data.user_type").type(STRING).description("응답 데이터"))
						// .requestSchema(Schema.schema("FormParameter-HealthCheck"))
						// .responseSchema(Schema.schema("HealthCheckResponse.health"))
						.build()
				)
			)).andExpect(status().isOk());
	}
}
