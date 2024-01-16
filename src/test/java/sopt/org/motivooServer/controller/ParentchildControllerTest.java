package sopt.org.motivooServer.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.*;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sopt.org.motivooServer.domain.health.dto.request.OnboardingRequest;
import sopt.org.motivooServer.domain.health.dto.response.CheckOnboardingResponse;
import sopt.org.motivooServer.domain.health.dto.response.OnboardingResponse;
import sopt.org.motivooServer.domain.parentchild.controller.ParentChildController;
import sopt.org.motivooServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.motivooServer.global.response.ApiResponse;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static java.sql.JDBCType.BOOLEAN;
import static java.util.TimeZone.LONG;
import static javax.management.openmbean.SimpleType.STRING;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static sopt.org.motivooServer.global.response.SuccessType.CHECK_ONBOARDING_INFO_SUCCESS;
import static sopt.org.motivooServer.global.response.SuccessType.ONBOARDING_SUCCESS;
import static sopt.org.motivooServer.util.ApiDocumentUtil.getDocumentRequest;
import static sopt.org.motivooServer.util.ApiDocumentUtil.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

@Slf4j
@DisplayName("ParentchildController 테스트")
@WebMvcTest(ParentchildControllerTest.class)
public class ParentchildControllerTest extends BaseControllerTest {

    private static final String DEFAULT_URL = "/user";
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

        List<String> exerciseNote = new ArrayList<>();
        exerciseNote.add("허리");
        exerciseNote.add("목");

        //when
        OnboardingRequest request = new OnboardingRequest("자녀", 25, true, "고강도 운동", "3일 - 5일 이내", "30분 미만", exerciseNote);
        log.info("유저 정보="+request.age()+" "+request.exerciseNote().get(0));
        when(parentChildController.onboardInput(request, principal)).thenReturn(result);

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.post(DEFAULT_URL+"/exercise")
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
                                                fieldWithPath("exercise_note").type(JsonFieldType.ARRAY).description("운동 유의사항")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.user_id").type(LONG).description("유저 아이디"),
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
        mockMvc.perform(get(DEFAULT_URL+"/onboarding")
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
                )).andExpect(MockMvcResultMatchers.status().isOk());
    }


}
