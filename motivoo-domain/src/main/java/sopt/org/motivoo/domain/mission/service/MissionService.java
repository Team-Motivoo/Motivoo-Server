package sopt.org.motivoo.domain.mission.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.mission.repository.MissionRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MissionService {
	@PersistenceContext
	private EntityManager entityManager;

	private final MissionRepository missionRepository;
	private static final String DEFAULT_ICON = "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/icon/icon_mission_body.png";

	@Transactional
	public void updateMissionContentText() {
		List<Mission> missions = missionRepository.findAll().stream()
			.filter(m -> m.getContent().contains("걷고 "))
			.peek(Mission::updateContentText)
			.collect(Collectors.toList());

		missionRepository.saveAll(missions);
	}

	@Transactional
	public void updateMissionDefaultIcon(){
		missionRepository.updateIcon(DEFAULT_ICON);
		entityManager.flush();
		entityManager.clear();
	}
}
