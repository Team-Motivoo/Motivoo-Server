package sopt.org.motivooServer.global.healthcheck;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.mission.service.UserMissionService;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {

	private final UserMissionService userMissionService;

	@PostMapping("/history/{parentchildId}")
	public ResponseEntity<Object> getHistoryDummy(@PathVariable final Long parentchildId) {
		userMissionService.demoHistory(parentchildId);
		return ResponseEntity.ok("운동 모아보기 더미 데이터 쌓기 성공!");
	}
}
