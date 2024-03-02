package sopt.org.motivoo.batch.service.firebase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.user.repository.UserRetriever;
import sopt.org.motivoo.external.firebase.FirebaseService;

@Component
@RequiredArgsConstructor
public class UserStepManager {

	private final FirebaseService firebaseService;
	private final UserRetriever userRetriever;

	public void insertUserStep() {
		Map<String, Integer> userSteps = new HashMap<>();
		userRetriever.findAll()
			.forEach(user -> userSteps.put(user.getId().toString(), 0));
		firebaseService.insertFBData(userSteps);
	}

	public Map<String, Integer> selectUserStep(List<Long> ids) {
		return firebaseService.selectFBData(ids);
	}

	public void insertUserStepById(Long id) {
		firebaseService.updateFBData(id);
	}

	public void deleteUserStep(Long id) {
		Map<String, Object> userSteps = new HashMap<>();
		userSteps.put(id.toString(), null);  // null을 전달하면 지정된 위치에서 데이터가 삭제됨

	}

}
