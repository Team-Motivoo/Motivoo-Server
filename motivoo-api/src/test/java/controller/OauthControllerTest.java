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

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import fixture.UserFixture;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.api.controller.auth.OauthController;
import sopt.org.motivoo.api.controller.auth.dto.request.OauthTokenRequest;
import sopt.org.motivoo.api.controller.auth.dto.request.RefreshRequest;
import sopt.org.motivoo.api.controller.auth.dto.response.LoginResponse;
import sopt.org.motivoo.api.controller.auth.dto.response.OauthTokenResponse;
import sopt.org.motivoo.common.response.ApiResponse;
import sopt.org.motivoo.domain.user.repository.UserRepository;

@Slf4j
@DisplayName("OauthController 테스트")
@WebMvcTest(OauthController.class)
public class OauthControllerTest extends BaseControllerTest {
    private static final String TAG = "유저";

    @MockBean
    private OauthController oauthController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    Principal principal;

    @Test
    @DisplayName("로그인 테스트")
    void loginTest() throws Exception {

        //given
        LoginResponse response = UserFixture.createLoginResponse();
        ResponseEntity<ApiResponse<LoginResponse>> result = ApiResponse
                .success(LOGIN_SUCCESS, response);

        //when
        OauthTokenRequest request = UserFixture.createOauthTokenRequest();
        when(oauthController.login(request)).thenReturn(result);

        //then
        mockMvc.perform(post("/oauth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(
                document("로그인 API 성공 Example",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .description("소셜 로그인 API")
                                        .requestFields(
                                                fieldWithPath("social_access_token").type(STRING).description("access token"),
                                                fieldWithPath("token_type").type(STRING).description("소셜 플랫폼(kakao|apple)")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.id").type(STRING).description("유저 아이디"),
                                                fieldWithPath("data.nickname").type(STRING).description("유저 닉네임"),
                                                fieldWithPath("data.token_type").type(STRING).description("토큰 타입(Bearer)"),
                                                fieldWithPath("data.access_token").type(STRING).description("access token"),
                                                fieldWithPath("data.refresh_token").type(STRING).description("refresh token")
                                        ).build()
                        )
                )).andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void reissueTest() throws Exception {
        // given
        RefreshRequest request = new RefreshRequest(1L);
        OauthTokenResponse response = new OauthTokenResponse("eyJ0eXAiOiJKV1QiLCJhbrG", "eyJ0eXAiOiJKV1QcLCJdhbG");

        ResponseEntity<ApiResponse<OauthTokenResponse>> result = ApiResponse
                .success(
                        REISSUE_SUCCESS, response);

        // 헤더에 담길 토큰 값을 직접 설정
        String refreshToken = "eyasdfsfsdfds";

        // when
        when(oauthController.reissue(refreshToken, request)).thenReturn(result);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/oauth/reissue")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(document("토큰 재발급 API 성공 Example",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .description("토큰 재발급 API")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("JWT Access Token"))
                                        .requestFields(
                                                fieldWithPath("user_id").type(NUMBER).description("유저 아이디"))
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.access_token").type(STRING).description("access token"),
                                                fieldWithPath("data.refresh_token").type(STRING).description("refresh token"))
                                        .build())
                )).andExpect(status().isOk());

    }



    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() throws Exception {
        //given
        ResponseEntity<ApiResponse<Object>> result = ApiResponse.success(
                LOGOUT_SUCCESS);

        // 헤더에 담길 토큰 값을 직접 설정
        String accessToken = "eyasdfsfsdfds";

        //when
        when(oauthController.logout(accessToken)).thenReturn(result);

        //then
        mockMvc.perform(post("/oauth/logout")
                .header("Authorization", accessToken)
        ).andDo(
                document("로그아웃 API 성공 Example",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .description("소셜 로그인 로그아웃 API")
                                        .requestHeaders(headerWithName("Authorization")
                                                .description("access token")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부")
                                        )
                                        .build()
                        )
                )).andExpect(status().isOk());

    }

    @Test
    @DisplayName("회원탈퇴 테스트")
    void withdrawTest() throws Exception {

        //given
        ResponseEntity<ApiResponse<Object>> result = ApiResponse
                .success(SIGNOUT_SUCCESS);

        //when
        when(oauthController.signout(principal)).thenReturn(result);

        //then
        mockMvc.perform(delete("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .principal(principal)
        ).andDo(
                document("회원탈퇴 API 성공 Example",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .description("회원 탈퇴 API")
                                        .requestFields()
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부")
                                        ).build()
                        )
                )).andExpect(status().isOk());
    }
}
