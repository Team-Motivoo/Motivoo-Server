package sopt.org.motivooServer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import sopt.org.motivooServer.domain.auth.config.CustomJwtAuthenticationEntryPoint;
import sopt.org.motivooServer.domain.auth.config.JwtTokenProvider;
import sopt.org.motivooServer.domain.auth.config.RedisConfig;
import sopt.org.motivooServer.domain.auth.repository.TokenRedisRepository;
import sopt.org.motivooServer.global.util.slack.SlackUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"local", "deploy"})
class MotivooServerApplicationTests {

	@MockBean
	private SlackUtil slackUtil;

	@MockBean
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;

	@MockBean
	private TokenRedisRepository tokenRedisRepository;

	@MockBean
	private RedisConfig redisConfig;

	@Autowired
	private RedisTemplate<String ,String> redisTemplate;



	@Test
	void contextLoads() {
	}

}
