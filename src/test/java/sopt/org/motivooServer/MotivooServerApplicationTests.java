package sopt.org.motivooServer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import sopt.org.motivooServer.domain.auth.config.CustomJwtAuthenticationEntryPoint;
import sopt.org.motivooServer.domain.auth.config.JwtTokenProvider;
import sopt.org.motivooServer.domain.auth.config.RedisConfig;
import sopt.org.motivooServer.domain.auth.repository.TokenRedisRepository;
import sopt.org.motivooServer.global.util.slack.SlackUtil;

@SpringBootTest
@ActiveProfiles({"local", "deploy"})
class MotivooServerApplicationTests {

	@MockBean
	private SlackUtil slackUtil;

	@MockBean
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;

	@MockBean
	private RedisConfig redisConfig;

	@MockBean
	private TokenRedisRepository tokenRedisRepository;


	@Test
	void contextLoads() {
	}

}
