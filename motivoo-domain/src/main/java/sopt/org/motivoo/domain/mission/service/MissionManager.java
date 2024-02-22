package sopt.org.motivoo.domain.mission.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sopt.org.motivoo.domain.mission.entity.Mission;

@Component
@Transactional(readOnly = true)
public class MissionManager {

	@Transactional
	public void updateContentText(List<Mission> missions) {
		missions.stream()
			.filter(m -> m.getContent().contains("걷고 "))
			.peek(Mission::updateContentText)
			.collect(Collectors.toList());
	}
}
