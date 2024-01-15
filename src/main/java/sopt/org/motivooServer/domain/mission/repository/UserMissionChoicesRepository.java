package sopt.org.motivooServer.domain.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivooServer.domain.mission.entity.UserMissionChoices;
import sopt.org.motivooServer.domain.user.entity.User;

public interface UserMissionChoicesRepository extends JpaRepository<UserMissionChoices, Long> {

    void deleteByUser(User user);
}
