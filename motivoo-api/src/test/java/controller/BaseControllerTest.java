package controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import sopt.org.motivoo.api.ApiApplication;
import sopt.org.motivoo.domain.auth.config.CustomJwtAuthenticationEntryPoint;
import sopt.org.motivoo.domain.auth.config.RedisConfig;
import sopt.org.motivoo.domain.auth.config.jwt.JwtTokenProvider;
import sopt.org.motivoo.domain.auth.repository.TokenRedisRepository;
import sopt.org.motivoo.external.client.auth.apple.service.AppleClaimsValidator;
import sopt.org.motivoo.external.client.auth.apple.service.AppleLoginService;
import sopt.org.motivoo.external.firebase.FirebaseService;
import sopt.org.motivoo.external.firebase.config.FirebaseConfig;
import sopt.org.motivoo.external.s3.S3Service;
import sopt.org.motivoo.external.s3.config.AWSConfig;
import sopt.org.motivoo.domain.external.slack.SlackService;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ContextConfiguration(classes = ApiApplication.class)
@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(properties = "spring.config.location=classpath:/application.yml")
public abstract class BaseControllerTest {

	@Autowired
	protected WebApplicationContext ctx;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected MockMvc mockMvc;

	@MockBean
	private RedisConfig redisConfig;

	@MockBean
	private TokenRedisRepository tokenRedisRepository;


	@MockBean
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;

	@MockBean
	private SlackService slackService;

	@MockBean
	private AWSConfig awsConfig;

	@MockBean
	private S3Service s3Service;

	@MockBean
	private FirebaseConfig firebaseConfig;

	@MockBean
	private FirebaseService firebaseService;

	@MockBean
	private AppleLoginService appleLoginService;

	@MockBean
	private AppleClaimsValidator appleClaimsValidator;

	@BeforeEach
	void setUp(final RestDocumentationContextProvider restDocumentation) {
		mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
			.apply(documentationConfiguration(restDocumentation))
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();
	}
}
