package sopt.org.motivooServer.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sopt.org.motivooServer.global.response.SuccessType.*;
import static sopt.org.motivooServer.util.ApiDocumentUtil.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.health.dto.request.OnboardingRequest;
import sopt.org.motivooServer.domain.health.dto.response.CheckOnboardingResponse;
import sopt.org.motivooServer.domain.health.dto.response.OnboardingResponse;
import sopt.org.motivooServer.domain.parentchild.controller.ParentChildController;
import sopt.org.motivooServer.domain.parentchild.dto.request.InviteRequest;
import sopt.org.motivooServer.domain.parentchild.dto.response.InviteResponse;
import sopt.org.motivooServer.domain.parentchild.dto.response.MatchingResponse;
import sopt.org.motivooServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.motivooServer.fixture.UserFixture;
import sopt.org.motivooServer.global.response.ApiResponse;


@Slf4j
@DisplayName("ParentchildController 테스트")
@WebMvcTest(ParentChildController.class)
public class ParentchildControllerTest extends BaseControllerTest {

    private final String TAG = "parentchild";

    @MockBean
    ParentChildController parentChildController;

    @MockBean
    ParentchildRepository parentchildRepository;

    @MockBean
    Principal principal;

    @Test
    @DisplayName("온보딩 정보 입력 테스트")
    void onboardingInputTest() throws Exception {

        //given
        OnboardingResponse response = new OnboardingResponse(1L, "aaaaaaaa", "고수");
        ResponseEntity<ApiResponse<OnboardingResponse>> result = ApiResponse
                .success(ONBOARDING_SUCCESS,response);

//        List<String> exerciseNote = new ArrayList<>();
//        exerciseNote.add("허리");
//        exerciseNote.add("목");

        //when
        OnboardingRequest request = UserFixture.createOnboardingRequest();
        log.info("유저 정보="+request.age()+" "+request.exerciseNote().get(0));
        when(parentChildController.onboardInput(request, principal)).thenReturn(result);

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/user/exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(principal)
        ).andDo(
                document("온보딩 정보 입력 API 성공 Example",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .description("온보딩시 유저와 운동 관련 정보를 입력하는 API")
                                        .requestFields(
                                                fieldWithPath("type").type(STRING).description("유저 타입(자녀|부모"),
                                                fieldWithPath("age").type(NUMBER).description("나이"),
                                                fieldWithPath("is_exercise").type(BOOLEAN).description("운동 유무"),
                                                fieldWithPath("exercise_type").type(STRING).description("운동 유형(고강도|중강도|저강도"),
                                                fieldWithPath("exercise_count").type(STRING).description("운동 일수"),
                                                fieldWithPath("exercise_time").type(STRING).description("운동 시간"),
                                                fieldWithPath("exercise_note[]").type(ARRAY).description("운동 유의사항")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.user_id").type(NUMBER).description("유저 아이디"),
                                                fieldWithPath("data.invite_code").type(STRING).description("초대 코드"),
                                                fieldWithPath("data.exercise_level").type(STRING).description("유저 분류(초보|중수|고수")
                                                ).build()
                                        )
                )).andExpect(MockMvcResultMatchers.status().isCreated()); //201 OK
    }

    @Test
    @DisplayName("온보딩 정보 입력 확인 테스트")
    void checkOnboardingInfo() throws Exception {
        //given
        CheckOnboardingResponse response = new CheckOnboardingResponse(true);
        ResponseEntity<ApiResponse<CheckOnboardingResponse>> result = ApiResponse
                .success(CHECK_ONBOARDING_INFO_SUCCESS, response);
        //when
        when(parentChildController.checkOnboardingInfo(principal)).thenReturn(result);

        //then
        mockMvc.perform(get("/user/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .principal(principal)
        ).andDo(
                document("온보딩 정보 입력 확인 Example",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .description("이전에 온보딩 정보를 입력한 이력이 있는지 여부를 알려주는 API")
                                        .requestFields()
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.is_finished_onboarding").type(BOOLEAN).description("유저의 타입(PARENT|CHILD)"))
                                        .build()
                        )
                )).andExpect(status().isOk());
    }

    @Test
    @DisplayName("초대 코드 입력(매칭) 테스트")
    void validateInviteCode() throws Exception {
        //given
        InviteRequest request = new InviteRequest("aaaaaaaa");
        InviteResponse response = new InviteResponse(1L, false, false, false);

        ResponseEntity<ApiResponse<InviteResponse>> result = ApiResponse.success(
                INPUT_INVITE_CODE_SUCCESS, response);
        //when
        when(parentChildController.validateInviteCode(request, principal)).thenReturn(result);

        //then
        mockMvc.perform(patch("/parentchild/match")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(principal)
        ).andDo(
                document("초대 코드 입력 API 성공 Example",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .description("초대 코드 입력 후 부모-자식 관계 매칭하는 API")
                                        .requestFields(
                                                fieldWithPath("invite_code").type(JsonFieldType.STRING).description("제공받은 초대 코드")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.user_id").type(NUMBER).description("유저 아이디"),
                                                fieldWithPath("data.is_matched").type(BOOLEAN).description("매칭 여부"),
                                                fieldWithPath("data.my_invite_code").type(BOOLEAN).description("내가 발급한 코드인지 판별"),
                                                fieldWithPath("data.is_finished_onboarding").type(BOOLEAN).description("온보딩 정보했는지 여부"))

                                .build()
                        )
                )).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @DisplayName("매칭 여부 확인 테스트")
    void validateMatching() throws Exception {
        //given
        MatchingResponse response = new MatchingResponse(true, 1L, 7L);
        ResponseEntity<ApiResponse<MatchingResponse>> result = ApiResponse
                .success(MATCHING_SUCCESS, response);
        //when
        when(parentChildController.validateMatching(principal)).thenReturn(result);

        //then
        mockMvc.perform(get("/onboarding/match")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .principal(principal)
        ).andDo(
                document("매칭 여부 확인 Example",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .description("유저의 부모-자녀 매칭을 확인하는 API")
                                        .requestFields()
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.is_matched").type(BOOLEAN).description("매칭 여부"),
                                                fieldWithPath("data.user_id").type(NUMBER).description("유저 자신의 아이디"),
                                                fieldWithPath("data.opponent_user_id").type(NUMBER).description("매칭된 상대 유저의 아이디"))
                                        .build()
                        )
                )).andExpect(status().isOk());
    }


}
