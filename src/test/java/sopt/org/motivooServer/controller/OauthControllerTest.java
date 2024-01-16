package sopt.org.motivooServer.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sopt.org.motivooServer.domain.auth.controller.OauthController;
import sopt.org.motivooServer.domain.auth.dto.request.OauthTokenRequest;
import sopt.org.motivooServer.domain.auth.dto.response.LoginResponse;
import sopt.org.motivooServer.domain.user.repository.UserRepository;
import sopt.org.motivooServer.global.response.ApiResponse;
import java.security.Principal;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static java.util.TimeZone.LONG;
import static javax.management.openmbean.SimpleType.STRING;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static sopt.org.motivooServer.global.response.SuccessType.LOGIN_SUCCESS;
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
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 메세지"),
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.id").type(LONG).description("유저 아이디"),
                                                fieldWithPath("data.nickname").type(STRING).description("유저 닉네임"),
                                                fieldWithPath("data.token_type").type(STRING).description("토큰 타입(Bearer)"),
                                                fieldWithPath("data.access_token").type(STRING).description("access token"),
                                                fieldWithPath("data.refresh_token").type(STRING).description("refresh token")
                                        ).build()
                        )
                )).andExpect(MockMvcResultMatchers.status().isOk());
    }
}
