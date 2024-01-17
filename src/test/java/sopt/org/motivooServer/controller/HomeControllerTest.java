package sopt.org.motivooServer.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static sopt.org.motivooServer.global.response.SuccessType.*;
import static sopt.org.motivooServer.util.ApiDocumentUtil.*;

import java.security.Principal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.mission.controller.HomeController;
import sopt.org.motivooServer.domain.mission.dto.request.MissionStepStatusRequest;
import sopt.org.motivooServer.domain.mission.dto.response.MissionStepStatusResponse;
import sopt.org.motivooServer.global.response.ApiResponse;

@Slf4j
@DisplayName("HomeController 테스트")
@WebMvcTest(HomeController.class)
public class HomeControllerTest extends BaseControllerTest{

	private static final String DEFAULT_URL = "/home";
	private static final String TAG = "유저미션";

	@MockBean
	private HomeController homeController;

	@MockBean
	private Principal principal;

	@Test
	@DisplayName("홈 화면 미션 달성 상태 조회 API 테스트")
	void getMissionCompleted() throws Exception {

		// given
		MissionStepStatusRequest request = new MissionStepStatusRequest(14000, 3000);
		MissionStepStatusResponse response =  MissionStepStatusResponse.builder()
			.userType("CHILD")
			.userId(2L)
			.userGoalStepCount(10000)
			.opponentUserId(3L)
			.opponentUserGoalStepCount(20000)
			.isStepCountCompleted(false)
			.isOpponentUserWithdraw(false).build();

		ResponseEntity<ApiResponse<MissionStepStatusResponse>> result = ApiResponse.success(
			GET_MISSION_IMAGE_PRE_SIGNED_URL_SUCCESS, response);

		// when
		when(homeController.getMissionCompleted(request, principal)).thenReturn(result);

		// then
		mockMvc.perform(patch(DEFAULT_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			.principal(principal)
		).andDo(
			document("홈 화면 미션 달성 상태 조회 API 성공 Example",
				getDocumentRequest(),
				getDocumentResponse(),
				resource(
					ResourceSnippetParameters.builder()
						.tag(TAG)
						.description("홈 화면 조회 시 걸음 수 요청값에 대한 미션 상태 업데이트 결과 및 부모-자녀 유저의 목표 걸음 수 반환")
						.requestFields(
							fieldWithPath("my_step_count").type(NUMBER).description("자신의 걸음 수"),
							fieldWithPath("opponent_step_count").type(NUMBER).description("상대 측(부모/자녀)의 걸음 수")
						)
						.responseFields(
							fieldWithPath("code").type(NUMBER).description("상태 코드"),
							fieldWithPath("message").type(STRING).description("상태 메세지"),
							fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
							fieldWithPath("data").description("응답 데이터"),
							fieldWithPath("data.user_type").type(STRING).description("유저의 타입(부모|자녀)"),
							fieldWithPath("data.user_id").type(NUMBER).description("유저 자신의 아이디"),
							fieldWithPath("data.user_goal_step_count").type(NUMBER).description("유저 오늘의 미션 목표 걸음 수 "),
							fieldWithPath("data.opponent_user_id").type(NUMBER).description("매칭된 상대 유저의 아이디"),
							fieldWithPath("data.opponent_user_goal_step_count").type(NUMBER).description("상대 유저 오늘의 미션 목표 걸음 수"),
							fieldWithPath("data.is_step_count_completed").type(BOOLEAN).description("걸음 수 달성 여부 → 운동 인증하기 버튼 활성화"),
							fieldWithPath("data.is_opponent_user_withdraw").type(BOOLEAN).description("상대 유저의 탈퇴 여부"))
						.build()
				)
			)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	
}
