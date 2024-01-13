package sopt.org.motivooServer.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sopt.org.motivooServer.global.response.SuccessType.*;
import static sopt.org.motivooServer.util.ApiDocumentUtil.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.auth.config.UserAuthentication;
import sopt.org.motivooServer.domain.user.controller.UserController;
import sopt.org.motivooServer.domain.user.dto.response.MyHealthInfoResponse;
import sopt.org.motivooServer.domain.user.dto.response.MyPageInfoResponse;
import sopt.org.motivooServer.domain.user.repository.UserRepository;
import sopt.org.motivooServer.global.response.ApiResponse;
import sopt.org.motivooServer.util.ApiDocumentUtil;

@Slf4j
@WithMockUser(roles = "USER")
@DisplayName("UserController 테스트")
@WebMvcTest(UserController.class)
public class UserControllerTest extends BaseControllerTest {

	protected static final String DEFAULT_URL = "/user";
	private final String TAG = "유저";

	@MockBean
	UserController userController;

	@MockBean
	UserRepository userRepository;

	@MockBean
	Principal principal;


	@Test
	@DisplayName("마이페이지 내 정보 조회 테스트")
	void userControllerMyPageInfoTest() throws Exception {

		// given
		Long userId = 1L;
		MyPageInfoResponse response = MyPageInfoResponse.builder()
			.userNickname("모티뿡뿡이")
			.userAge(20)
			.userType("CHILD").build();
		ResponseEntity<ApiResponse<MyPageInfoResponse>> result = ApiResponse
			.success(GET_MYINFO_SUCCESS, response);

		// when
		when(userController.getMyInfo(principal)).thenReturn(result);

		// then
		mockMvc.perform(get(DEFAULT_URL + "/me")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.principal(principal)
		).andDo(
			document("마이페이지 내 정보 조회 API 성공 Example",
				getDocumentRequest(),
				getDocumentResponse(),
				resource(
					ResourceSnippetParameters.builder()
						.tag(TAG)
						.description("마이페이지 내 정보 조회")
						.requestFields()
						.responseFields(
							fieldWithPath("code").type(NUMBER).description("상태 코드"),
							fieldWithPath("message").type(STRING).description("상태 메세지"),
							fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
							fieldWithPath("data").type(OBJECT).description("응답 데이터"),
							fieldWithPath("data.user_nickname").type(STRING).description("유저 닉네임"),
							fieldWithPath("data.user_age").type(NUMBER).description("유저 나이"),
							fieldWithPath("data.user_type").type(STRING).description("유저 타입 (부모/자식)"))
						.build()
				)
			)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("마이페이지 운동정보 조회 테스트")
	void userControllerMyHealthInfoTest() throws Exception {

		// given
		Long userId = 1L;
		MyHealthInfoResponse response = MyHealthInfoResponse.builder()
			.isExercise(true)
			.exerciseType("고강도")
			.exerciseFrequency("3일")
			.exerciseTime("2~3시간")
			.healthNotes(Arrays.asList("목", "어깨")).build();
		ResponseEntity<ApiResponse<MyHealthInfoResponse>> result = ApiResponse
			.success(GET_MYPAGE_HEALTH_INFO_SUCCESS, response);

		// when
		when(userController.getMyExercise(principal)).thenReturn(result);

		// then
		mockMvc.perform(get(DEFAULT_URL + "/exercise")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.principal(principal)
		).andDo(
			document("마이페이지 운동정보 조회 API 성공 Example",
				getDocumentRequest(),
				getDocumentResponse(),
				resource(
					ResourceSnippetParameters.builder()
						.tag(TAG)
						.description("마이페이지 운동정보 조회")
						.requestFields()
						.responseFields(
							fieldWithPath("code").type(NUMBER).description("상태 코드"),
							fieldWithPath("message").type(STRING).description("상태 메세지"),
							fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
							fieldWithPath("data").type(OBJECT).description("응답 데이터"),
							fieldWithPath("data.is_exercise").type(BOOLEAN).description("운동 여부"),
							fieldWithPath("data.exercise_type").type(STRING).description("운동 유형"),
							fieldWithPath("data.exercise_frequency").type(STRING).description("주 평균 운동 횟수"),
							fieldWithPath("data.exercise_time").type(STRING).description("하루 평균 운동 시간"),
							fieldWithPath("data.health_notes[]").type(ARRAY).description("건강 주의사항"))
						.build()
				)
			)).andExpect(status().isOk());
	}
}
