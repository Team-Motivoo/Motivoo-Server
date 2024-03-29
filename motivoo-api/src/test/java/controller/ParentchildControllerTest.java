package controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sopt.org.motivoo.common.response.SuccessType.*;
import static util.ApiDocumentUtil.*;

import java.security.Principal;

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

import fixture.HealthFixture;
import fixture.ParentchildFixture;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.api.controller.health.dto.request.OnboardingRequest;
import sopt.org.motivoo.api.controller.health.dto.response.CheckOnboardingResponse;
import sopt.org.motivoo.api.controller.health.dto.response.OnboardingResponse;
import sopt.org.motivoo.api.controller.parentchild.ParentChildController;
import sopt.org.motivoo.api.controller.parentchild.dto.request.InviteRequest;
import sopt.org.motivoo.api.controller.parentchild.dto.response.InviteReceiveResponse;
import sopt.org.motivoo.api.controller.parentchild.dto.response.InviteSendResponse;
import sopt.org.motivoo.api.controller.parentchild.dto.response.MatchingResponse;
import sopt.org.motivoo.common.response.ApiResponse;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.parentchild.repository.ParentchildRepository;


@Slf4j
@DisplayName("ParentchildController 테스트")
@WebMvcTest(ParentChildController.class)
public class ParentchildControllerTest extends BaseControllerTest {

    private static final String TAG = "온보딩";

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
        OnboardingRequest request = HealthFixture.createOnboardingRequest();
        OnboardingResponse response = HealthFixture.createOnboardingResponse();
        ResponseEntity<ApiResponse<OnboardingResponse>> result = ApiResponse
                .success(ONBOARDING_SUCCESS,response);

//        List<String> exerciseNote = new ArrayList<>();
//        exerciseNote.add("허리");
//        exerciseNote.add("목");

        //when
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
                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
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
        Parentchild parentchild = ParentchildFixture.createParentchild();
        InviteRequest request = new InviteRequest(parentchild.getInviteCode());
        InviteReceiveResponse response = new InviteReceiveResponse(1L, 2L, parentchild.isMatched());

        ResponseEntity<ApiResponse<InviteReceiveResponse>> result = ApiResponse.success(
                INPUT_INVITE_CODE_SUCCESS, response);
        //when
        when(parentChildController.matchParentchildRelation(request, principal)).thenReturn(result);

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
                                                fieldWithPath("invite_code").type(STRING).description("제공받은 초대 코드")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.user_id").type(NUMBER).description("유저 아이디"),
                                                fieldWithPath("data.opponent_user_id").type(NUMBER).description("매칭 상대 유저 아이디"),
                                                fieldWithPath("data.is_matched").type(BOOLEAN).description("매칭 성공 여부"))

                                .build()
                        )
                )).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @DisplayName("초대 코드 보내기 테스트")
    void sendInviteCode() throws Exception {
        //given
        Parentchild parentchild = ParentchildFixture.createParentchild();
        InviteSendResponse response = new InviteSendResponse(1L, true, parentchild.getInviteCode());

        ResponseEntity<ApiResponse<InviteSendResponse>> result = ApiResponse.success(
            SEND_INVITE_CODE_SUCCESS, response);
        //when
        when(parentChildController.inviteParentchild(principal)).thenReturn(result);

        //then
        mockMvc.perform(post("/parentchild/invite")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .principal(principal)
        ).andDo(
            document("초대 코드 보내기 API 성공 Example",
                getDocumentRequest(),
                getDocumentResponse(),
                resource(
                    ResourceSnippetParameters.builder()
                        .tag(TAG)
                        .description("초대 코드 입력 후 부모-자식 관계 매칭하는 API")
                        .requestFields()
                        .responseFields(
                            fieldWithPath("code").type(NUMBER).description("상태 코드"),
                            fieldWithPath("message").type(STRING).description("상태 메세지"),
                            fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
                            fieldWithPath("data").description("응답 데이터"),
                            fieldWithPath("data.user_id").type(NUMBER).description("유저 아이디"),
                            fieldWithPath("data.is_matched").type(BOOLEAN).description("매칭 여부"),
                            fieldWithPath("data.invite_code").type(STRING).description("초대 코드"))

                        .build()
                )
            )).andExpect(MockMvcResultMatchers.status().isCreated());

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
