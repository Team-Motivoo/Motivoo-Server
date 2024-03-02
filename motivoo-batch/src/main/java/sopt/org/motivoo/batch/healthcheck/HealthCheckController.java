package sopt.org.motivoo.batch.healthcheck;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

	@GetMapping
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("test success!");
	}
}
