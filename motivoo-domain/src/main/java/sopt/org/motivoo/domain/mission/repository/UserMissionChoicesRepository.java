package sopt.org.motivoo.domain.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivoo.domain.mission.entity.UserMissionChoices;
import sopt.org.motivoo.domain.user.entity.User;

public interface UserMissionChoicesRepository extends JpaRepository<UserMissionChoices, Long> {

    void deleteByUser(User user);
}
