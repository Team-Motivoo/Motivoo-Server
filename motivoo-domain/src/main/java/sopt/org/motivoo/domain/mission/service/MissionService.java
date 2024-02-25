package sopt.org.motivoo.domain.mission.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.mission.repository.MissionRetriever;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MissionService {

	@PersistenceContext
	private EntityManager em;

	private final MissionRetriever missionRetriever;
	private final MissionManager missionManager;
	private static final String DEFAULT_ICON = "https://motivoo-server-bucket.s3.ap-northeast-2.amazonaws.com/icon/icon_mission_body.png";

	@Transactional
	public void updateMissionContentText() {
		List<Mission> missions = missionRetriever.getAllMissions();
		missionManager.updateContentText(missions);
		missionRetriever.saveAll(missions);
	}

	@Transactional
	public void updateMissionDefaultIcon(){
		missionRetriever.updateIconUrl(DEFAULT_ICON);
		em.flush();
		em.clear();
	}

}
