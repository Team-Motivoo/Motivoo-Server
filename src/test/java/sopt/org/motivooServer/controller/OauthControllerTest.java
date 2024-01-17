package sopt.org.motivooServer.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sopt.org.motivooServer.global.response.SuccessType.*;

import java.security.Principal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.auth.controller.OauthController;
import sopt.org.motivooServer.domain.auth.dto.request.OauthTokenRequest;
import sopt.org.motivooServer.domain.auth.dto.response.LoginResponse;
import sopt.org.motivooServer.domain.user.repository.UserRepository;
import sopt.org.motivooServer.global.response.ApiResponse;



import static sopt.org.motivooServer.util.ApiDocumentUtil.getDocumentRequest;
import static sopt.org.motivooServer.util.ApiDocumentUtil.getDocumentResponse;

@Slf4j
@DisplayName("OauthController 테스트")
@WebMvcTest(OauthController.class)
public class OauthControllerTest extends BaseControllerTest{
    private final String TAG = "oauth";

    @MockBean
    OauthController oauthController;

    @MockBean
    UserRepository userRepository;

    @MockBean
    Principal principal;

    @Test
    @DisplayName("로그인 테스트")
    void loginTest() throws Exception {

        //given
        LoginResponse response = LoginResponse.builder()
                .id("1")
                .nickname("모티뿌")
                .accessToken("Bearer")
                .accessToken("eyJ0eXAiOiJKV1QiLCJhbG")
                .tokenType("Bearer")
                .refreshToken("eyJ0eXAiOiJKV1QiLCJhbG")
                .build();
        ResponseEntity<ApiResponse<LoginResponse>> result = ApiResponse
                .success(LOGIN_SUCCESS, response);

        //when
        OauthTokenRequest request = new OauthTokenRequest("eyJ0eXAiOiJKV1QiLCJhbG", "kakao");
        when(oauthController.login(request)).thenReturn(result);

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/oauth/login")
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

//    @Test
//    @DisplayName("토큰 재발급 테스트")
//    void reissueTest() throws Exception {
//        // given
//        RefreshRequest request = new RefreshRequest(1L);
//        OauthTokenResponse response = new OauthTokenResponse("eyJ0eXAiOiJKV1QiLCJhbrG", "eyJ0eXAiOiJKV1QiLCJdhbG");
//
//        // ApiResponse에 응답 데이터 추가
//        ResponseEntity<ApiResponse<OauthTokenResponse>> result = ApiResponse.success(REISSUE_SUCCESS, response);
//
//        // when
//        when(oauthController.reissue("eyJ0eXAiOiJKV1QiLCJhbG", request)).thenReturn(result);
//
//        // then
//        mockMvc.perform(RestDocumentationRequestBuilders.post("/oauth/reissue")
//                .header("Authorization", "Bearer {refresh token}")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request))
//        ).andDo(
//                document("토큰 재발급 API 성공 Example",
//                        getDocumentRequest(),
//                        getDocumentResponse(),
//                        resource(
//                                ResourceSnippetParameters.builder()
//                                        .tag(TAG)
//                                        .description("토큰 재발급 API")
//                                        .requestHeaders(headerWithName("Authorization")
//                                                .description("refresh token")
//                                        )
//                                        .requestFields(
//                                                fieldWithPath("user_id").type(NUMBER).description("유저 아이디")
//                                        )
//                                        .responseFields(
//                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
//                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
//                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
//                                                fieldWithPath("data").description("응답 데이터"),
//                                                fieldWithPath("data.access_token").type(STRING).description("access token"),
//                                                fieldWithPath("data.refresh_token").type(STRING).description("refresh token")
//                                        ).build()
//                        )
//                )).andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("로그아웃 테스트")
//    void logoutTest() throws Exception {
//        //given
//        ResponseEntity<ApiResponse<Object>> result = ApiResponse.success(
//                LOGOUT_SUCCESS);
//        //when
//        when(oauthController.logout("access token")).thenReturn(result);
//
//        //then
//        mockMvc.perform(RestDocumentationRequestBuilders.post("/oauth/logout")
//                .header("Authorization", "Bearer access token")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//        ).andDo(
//                document("로그아웃 API 성공 Example",
//                        getDocumentRequest(),
//                        getDocumentResponse(),
//                        resource(
//                                ResourceSnippetParameters.builder()
//                                        .tag(TAG)
//                                        .description("소셜 로그인 로그아웃 API")
//                                        .requestHeaders(headerWithName("Authorization")
//                                                .description("access token")
//                                        )
//                                        .responseFields(
//                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
//                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
//                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부")
//                                        )
//                                        .build()
//                        )
//                )).andExpect(status().isOk());
//
//    }

    @Test
    @DisplayName("회원탈퇴 테스트")
    void withdrawTest() throws Exception {

        //given
        OauthTokenRequest request = new OauthTokenRequest("TJ-tINCO5zPBhmFudnfm-Sjn4H3jtOe8G3IKKiWRAAABjPxmAbhDz1szkZmFRA","kakao");
        ResponseEntity<ApiResponse<Object>> result = ApiResponse
                .success(SIGNOUT_SUCCESS);

        //when
        when(oauthController.signout(principal)).thenReturn(result);

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(principal)
        ).andDo(
                document("회원탈퇴 API 성공 Example",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .description("회원 탈퇴 API")
                                        .requestFields(
                                                fieldWithPath("social_access_token").type(STRING).description("access token"),
                                                fieldWithPath("token_type").type(STRING).description("소셜 플랫폼(kakao|apple)")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                                fieldWithPath("message").type(STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부")
                                        ).build()
                        )
                )).andExpect(status().isOk());
    }
}
