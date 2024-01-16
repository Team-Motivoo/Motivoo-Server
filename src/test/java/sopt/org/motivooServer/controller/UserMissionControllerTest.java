package sopt.org.motivooServer.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static sopt.org.motivooServer.global.external.s3.S3BucketDirectory.*;
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
import sopt.org.motivooServer.domain.mission.controller.UserMissionController;
import sopt.org.motivooServer.domain.mission.dto.request.MissionImgUrlRequest;
import sopt.org.motivooServer.domain.mission.dto.request.TodayMissionChoiceRequest;
import sopt.org.motivooServer.domain.mission.dto.response.MissionHistoryResponse;
import sopt.org.motivooServer.domain.mission.dto.response.MissionImgUrlResponse;
import sopt.org.motivooServer.domain.mission.dto.response.TodayMissionResponse;
import sopt.org.motivooServer.fixture.UserMissionFixture;
import sopt.org.motivooServer.global.response.ApiResponse;

@Slf4j
@DisplayName("UserMissionController 테스트")
@WebMvcTest(UserMissionController.class)
public class UserMissionControllerTest extends BaseControllerTest{

	private static final String DEFAULT_URL = "/mission";
	private static final String TAG = "유저미션";

	@MockBean
	private UserMissionController userMissionController;

	@MockBean
	private Principal principal;

	@Test
	@DisplayName("미션 인증 사진 등록 API 테스트")
	void getMissionImgPresignedUrl() throws Exception {

		// given
		MissionImgUrlRequest request = new MissionImgUrlRequest(MISSION_PREFIX.value());
		MissionImgUrlResponse response = MissionImgUrlResponse.of(
			"https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/mission/39e545f4-9ad8-4c05-a8cb-960d0787e776.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240109T051832Z&X-Amz-SignedHeaders=host&X-Amz-Expires=60&X-Amz-Credential=AKIASRIQXMUZKAKLJQGJ%2F20240109%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=0c98a5efeaea9bbf607becaaeb511495b68e83b9897b193ef542fa4a8c352dd4",
			"39e545f4-9ad8-4c05-a8cb-960d0787e776.jpg");

		ResponseEntity<ApiResponse<MissionImgUrlResponse>> result = ApiResponse.success(
			GET_MISSION_IMAGE_PRE_SIGNED_URL_SUCCESS, response);

		// when
		when(userMissionController.getMissionImgUrl(request, principal)).thenReturn(result);

		// then
		mockMvc.perform(patch(DEFAULT_URL + "/image")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			.principal(principal)
		).andDo(
			document("미션 인증 사진 등록 API 성공 Example",
				getDocumentRequest(),
				getDocumentResponse(),
				resource(
					ResourceSnippetParameters.builder()
						.tag(TAG)
						.description("미션 인증 사진 업로드를 위한 PreSigned Url 반환")
						.requestFields(
							fieldWithPath("img_prefix").type(STRING).description("운동 인증 사진 디렉터리")
						)
						.responseFields(
							fieldWithPath("code").type(NUMBER).description("상태 코드"),
							fieldWithPath("message").type(STRING).description("상태 메세지"),
							fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
							fieldWithPath("data").description("응답 데이터"),
							fieldWithPath("data.img_presigned_url").type(STRING).description("S3 PreSigned Url"),
							fieldWithPath("data.file_name").type(STRING).description("이미지 파일명(UUID로 임의 지정)"))
						.build()
				)
			)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("운동 모아보기 API 테스트")
	void getMissionHistory() throws Exception {

		// given
		MissionHistoryResponse response = UserMissionFixture.createMissionHistoryResponse();

		ResponseEntity<ApiResponse<MissionHistoryResponse>> result = ApiResponse.success(
			GET_MISSION_HISTORY_SUCCESS, response);

		// when
		when(userMissionController.getUserMissionHistory(principal)).thenReturn(result);

		// then
		mockMvc.perform(get(DEFAULT_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.principal(principal)
		).andDo(
			document("운동 모아보기 API 성공 Example",
				getDocumentRequest(),
				getDocumentResponse(),
				resource(
					ResourceSnippetParameters.builder()
						.tag(TAG)
						.description("지난 30일 간의 운동 인증을 모아보는 API")
						.requestFields()
						.responseFields(
							fieldWithPath("code").type(NUMBER).description("상태 코드"),
							fieldWithPath("message").type(STRING).description("상태 메세지"),
							fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
							fieldWithPath("data").description("응답 데이터"),
							fieldWithPath("data.user_type").type(STRING).description("유저의 타입(PARENT|CHILD)"),
							fieldWithPath("data.today_mission").type(OBJECT).description("오늘의 미션"),
							fieldWithPath("data.today_mission.mission_content").type(STRING).description("오늘의 미션 내용"),
							fieldWithPath("data.mission_history").type(ARRAY).description("미션 히스토리"),
							fieldWithPath("data.mission_history[].date").type(STRING).description("미션 일자"),
							fieldWithPath("data.mission_history[].my_mission_content").type(STRING).description("나의 미션 내용"),
							fieldWithPath("data.mission_history[].my_mission_img_url").type(STRING).description("나의 미션 인증 이미지 url "),
							fieldWithPath("data.mission_history[].my_mission_status").type(STRING).description("나의 미션 달성 상태 (진행중 | 성공 | 실패)"),
							fieldWithPath("data.mission_history[].opponent_mission_content").type(STRING).description("상대측의 미션 내용"),
							fieldWithPath("data.mission_history[].opponent_mission_img_url").type(STRING).description("상대측의 미션 인증 이미지 url"),
							fieldWithPath("data.mission_history[].opponent_mission_status").type(STRING).description("상대측의 미션 달성 상태 (진행중 | 성공 | 실패)"))


						.build()
				)
			)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("오늘의 미션 조회 API 테스트")
	void getTodayMission() throws Exception {

		// given
		TodayMissionResponse response = UserMissionFixture.createTodayMissionResponse();

		ResponseEntity<ApiResponse<TodayMissionResponse>> result = ApiResponse.success(
			GET_TODAY_MISSION_SUCCESS, response);

		// when
		when(userMissionController.getTodayMission(principal)).thenReturn(result);

		// then
		mockMvc.perform(post(DEFAULT_URL + "/today/choice")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.principal(principal)
		).andDo(
			document("오늘의 미션 조회 API 성공 Example #1",
				getDocumentRequest(),
				getDocumentResponse(),
				resource(
					ResourceSnippetParameters.builder()
						.tag(TAG)
						.description("2가지 미션 선택지 또는 그 중 선정한 오늘의 미션을 조회하는 API")
						.requestFields()
						.responseFields(
							fieldWithPath("code").type(NUMBER).description("상태 코드"),
							fieldWithPath("message").type(STRING).description("상태 메세지"),
							fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
							fieldWithPath("data").description("응답 데이터"),
							fieldWithPath("data.is_choice_finished").type(BOOLEAN).description("미션 선정 여부 → false"),
							fieldWithPath("data.date").type(NULL).description("오늘의 날짜"),
							fieldWithPath("data.mission_choice_list").type(NULL).description("오늘의 미션 선택지 리스트"),
							// fieldWithPath("data.mission_choice_list[]").type(OBJECT).description(""),
							// fieldWithPath("data.mission_choice_list[].mission_id").type(NUMBER).description("미션 번호"),
							// fieldWithPath("data.mission_choice_list[].mission_content").type(STRING).description("미션 내용"),
							fieldWithPath("data.today_mission").type(OBJECT).description("오늘의 미션"),
							fieldWithPath("data.today_mission.mission_content").type(STRING).description("오늘의 미션 내용"),
							fieldWithPath("data.today_mission.mission_description").type(STRING).description("운동 방법 설명 링크"),
							fieldWithPath("data.today_mission.mission_step_count").type(NUMBER).description("오늘의 미션 걸음 수"),
							fieldWithPath("data.today_mission.mission_quest").type(STRING).description("흥미유발을 위한 랜덤 미션 퀘스트\n*사진 포즈"))

						.build()
				)
			))
			// document("오늘의 미션 조회 API 성공 Example #2",
			// 	getDocumentRequest(),
			// 	getDocumentResponse(),
			// 	resource(
			// 		ResourceSnippetParameters.builder()
			// 			.tag(TAG)
			// 			.description("2가지 미션 선택지 또는 그 중 선정한 오늘의 미션을 조회하는 API")
			// 			.requestFields(
			// 				fieldWithPath("mission_id").type(NUMBER).description("선택한 미션 번호")
			// 			)
			// 			.responseFields(
			// 				fieldWithPath("code").type(NUMBER).description("상태 코드"),
			// 				fieldWithPath("message").type(STRING).description("상태 메세지"),
			// 				fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
			// 				fieldWithPath("data").description("응답 데이터"),
			// 				fieldWithPath("data.is_choice_finished").type(BOOLEAN).description("미션 선정 여부 → false"),
			// 				fieldWithPath("data.mission_choice_list").type(ARRAY).description("오늘의 미션 선택지 리스트"),
			// 				fieldWithPath("data.mission_choice_list[].mission_id").type(NUMBER).description("미션 번호"),
			// 				fieldWithPath("data.mission_choice_list[].mission_content").type(STRING).description("미션 내용"),
			// 				fieldWithPath("data.today_mission").type(OBJECT).description("오늘의 미션"),
			// 				fieldWithPath("data.today_mission.mission_content").type(STRING).description("오늘의 미션 내용"),
			// 				fieldWithPath("data.today_mission.mission_description").type(STRING).description("운동 방법 설명 링크"),
			// 				fieldWithPath("data.today_mission.mission_quest").type(STRING).description("흥미유발을 위한 랜덤 미션 퀘스트\n*사진 포즈"))
			//
			// 			.build()
			// 	))

			.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	@DisplayName("오늘의 미션 선정 API 테스트")
	void choiceTodayMission() throws Exception {

		// given
		TodayMissionChoiceRequest request = new TodayMissionChoiceRequest(5L);
		ResponseEntity<ApiResponse<Object>> result = ApiResponse.success(
			CHOICE_TODAY_MISSION_SUCCESS);

		// when
		when(userMissionController.choiceTodayMission(request, principal)).thenReturn(result);

		// then
		mockMvc.perform(post(DEFAULT_URL + "/today")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			.principal(principal)
		).andDo(
			document("오늘의 미션 선정 API 성공 Example",
				getDocumentRequest(),
				getDocumentResponse(),
				resource(
					ResourceSnippetParameters.builder()
						.tag(TAG)
						.description("2가지 운동 미션 중 오늘의 미션을 선택하는 API")
						.requestFields(
							fieldWithPath("mission_id").type(NUMBER).description("선택한 미션 번호")
						)
						.responseFields(
							fieldWithPath("code").type(NUMBER).description("상태 코드"),
							fieldWithPath("message").type(STRING).description("상태 메세지"),
							fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"))
							// fieldWithPath("data").type(NULL).description("응답 데이터"))

						.build()
				)
			)).andExpect(MockMvcResultMatchers.status().isCreated());
	}


}
