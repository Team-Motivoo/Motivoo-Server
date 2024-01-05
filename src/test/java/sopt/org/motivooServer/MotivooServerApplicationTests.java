package sopt.org.motivooServer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import sopt.org.motivooServer.global.util.slack.SlackUtil;

@SpringBootTest
class MotivooServerApplicationTests {

	@MockBean
	private SlackUtil slackUtil;

	@Test
	void contextLoads() {
	}

}
