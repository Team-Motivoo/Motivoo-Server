package sopt.org.motivooServer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import sopt.org.motivooServer.global.util.slack.SlackUtil;

@SpringBootTest
@ActiveProfiles({"test"})
class MotivooServerApplicationTests {

	@MockBean
	private SlackUtil slackUtil;

	@Test
	void contextLoads() {
	}

}
