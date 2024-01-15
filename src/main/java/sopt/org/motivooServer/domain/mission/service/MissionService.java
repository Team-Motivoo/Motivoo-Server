package sopt.org.motivooServer.domain.mission.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.mission.entity.Mission;
import sopt.org.motivooServer.domain.mission.repository.MissionRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MissionService {

	private final MissionRepository missionRepository;

	@Transactional
	public void updateMissionContentText() {
		List<Mission> missions = missionRepository.findAll().stream()
			.filter(m -> m.getContent().contains("걷고 "))
			.peek(Mission::updateContentText)
			.collect(Collectors.toList());

		missionRepository.saveAll(missions);
	}
}
