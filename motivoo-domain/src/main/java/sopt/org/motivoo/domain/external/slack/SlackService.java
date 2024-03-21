package sopt.org.motivoo.domain.external.slack;

import static com.slack.api.model.block.composition.BlockCompositions.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.slack.api.Slack;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.BlockCompositions;
import com.slack.api.webhook.WebhookPayloads;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.common.response.SuccessType;
import sopt.org.motivoo.domain.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlackService {

	@Value("${slack.webhook.url}")
	private String errorUrl;
	@Value("${slack.webhook.success}")
	private String successUrl;

	private final static String NEW_LINE = "\n";
	private final static String DOUBLE_NEW_LINE = "\n\n";

	private StringBuilder sb = new StringBuilder();

	private final UserRepository userRepository;

	// Slack으로 알림 보내기
	public void sendAlert(Exception error, HttpServletRequest request) throws IOException {

		// 현재 프로파일이 특정 프로파일이 아니면 알림 보내지 않기

		// 메시지 내용인 LayoutBlock List 생성
		List layoutBlocks = generateLayoutBlock(error, request);

		// Slack의 sent API와 webhookURL을 통해 생성한 메시지 내용 전송
		Slack.getInstance().send(errorUrl, WebhookPayloads.payload(p ->
			p.username("Exception is detected 🚨")  // 메시지 전송 유저명
				.iconUrl("<https://yt3.googleusercontent.com/ytc/AGIKgqMVUzRrhoo1gDQcqvPo0PxaJz7e0gqDXT0D78R5VQ=s900-c-k-c0x00ffffff-no-rj>")  // 메시지 전송 유저 아이콘 이미지 URL
				.blocks(layoutBlocks)));  // 메시지 내용
	}

	public void sendSuccess(SuccessType successType) throws IOException {

		List<LayoutBlock> layoutBlocks = generateSignInBlock(successType);

		Slack.getInstance().send(successUrl, WebhookPayloads.payload(p ->
				p.username("모티뿡 알리미")
					.iconUrl("https://yt3.googleusercontent.com/ytc/AGIKgqMVUzRrhoo1gDQcqvPo0PxaJz7e0gqDXT0D78R5VQ=s900-c-k-c0x00ffffff-no-rj")
					.blocks(layoutBlocks)));
	}

	// 전체 메시지가 담긴 LayoutBlock 생성
	private List generateLayoutBlock(Exception error, HttpServletRequest request) {
		return Blocks.asBlocks(
			getHeader("서버 측 오류로 예상되는 예외 상황이 발생하였습니다."),
			Blocks.divider(),

			getSection(generateErrorMessage(error)),
			Blocks.divider(),

			getSection(generateErrorPointMessage(request)),
			Blocks.divider(),

			// 이슈 생성을 위해 바로가기 링크
			getSection("<https://github.com/Team-Motivoo/Motivoo-Server/issues|이슈 생성하러 가기>")
		);
	}

	// 예외 정보 메시지 생성
	private String generateErrorMessage(Exception error) {
		sb.setLength(0);
		sb.append("*[🔥 Exception]*" + NEW_LINE + error.toString() + DOUBLE_NEW_LINE);
		sb.append("*[📩 From]*" + NEW_LINE + readRootStackTrace(error) + DOUBLE_NEW_LINE);

		return sb.toString();
	}

	// HttpServletRequest를 사용하여 예외발생 요청에 대한 정보 메시지 생성
	private String generateErrorPointMessage(HttpServletRequest request) {
		sb.setLength(0);
		sb.append("*[🧾세부정보]*" + NEW_LINE);
		sb.append("Request URL: " + request.getRequestURL().toString() + NEW_LINE);
		sb.append("Request Method: " + request.getMethod() + NEW_LINE);
		sb.append("Request Time : " + new Date() + NEW_LINE);

		return sb.toString();
	}


	// 회원가입 성공 알림 LayoutBlock 생성
	private List<LayoutBlock> generateSignInBlock(SuccessType successType) {
		return Blocks.asBlocks(
			getHeader("💙새로운 유저가 가입했습니다."),
			Blocks.divider(),
			getSection(generateSuccessMessage(successType)),
			Blocks.divider(),
			getSection(generateSignInMessage()),
			Blocks.divider()
		);
	}

	private String generateSuccessMessage(SuccessType successType) {
		sb.setLength(0);
		sb.append("*[🎉축하합니다!]*" + NEW_LINE + "" + DOUBLE_NEW_LINE);

		return sb.toString();
	}

	private String generateSignInMessage() {
		sb.setLength(0);
		sb.append("*[🧾유저 가입 정보]*" + NEW_LINE);
		sb.append(userRepository.findCurrentUserId().intValue() + "번째 유저가 모티부와 가족이 되었어요👨‍👩‍👧‍👦");
		return sb.toString();
	}


	// 예외발생 클래스 정보 return
	private String readRootStackTrace(Exception error) {
		return error.getStackTrace()[0].toString();
	}

	// 에러 로그 메시지의 제목 return
	private LayoutBlock getHeader(String text) {
		return Blocks.header(h -> h.text(
			plainText(pt -> pt.emoji(true).text(text))
		));
	}

	// 에러 로그 메시지의 내용 return
	private LayoutBlock getSection(String message) {
		return Blocks.section(s -> s.text(
			BlockCompositions.markdownText(message)
		));
	}
}
