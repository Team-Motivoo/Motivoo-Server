package sopt.org.motivooServer.controller;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import sopt.org.motivooServer.domain.auth.config.CustomJwtAuthenticationEntryPoint;
import sopt.org.motivooServer.domain.auth.config.JwtTokenProvider;
import sopt.org.motivooServer.domain.auth.config.RedisConfig;
import sopt.org.motivooServer.domain.auth.repository.TokenRedisRepository;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(properties = "spring.config.location=classpath:/application-local.yml")
public abstract class BaseControllerTest {

	@Autowired
	protected WebApplicationContext ctx;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;

	@Autowired
	private RedisConfig redisConfig;

	@Autowired
	private TokenRedisRepository tokenRedisRepository;

	@BeforeEach
	void setUp(final RestDocumentationContextProvider restDocumentation) {
		mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
			.apply(documentationConfiguration(restDocumentation))
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();
	}
}
