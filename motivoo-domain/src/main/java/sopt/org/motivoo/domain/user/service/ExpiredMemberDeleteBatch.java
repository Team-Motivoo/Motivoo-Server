package sopt.org.motivoo.domain.user.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.health.repository.HealthRepository;
import sopt.org.motivoo.domain.mission.repository.UserMissionChoicesRepository;
import sopt.org.motivoo.domain.mission.repository.UserMissionRepository;
import sopt.org.motivoo.domain.parentchild.repository.ParentchildRepository;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ExpiredMemberDeleteBatch {
    private final UserRepository userRepository;
    private final ParentchildRepository parentchildRepository;
    private final HealthRepository healthRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserMissionChoicesRepository userMissionChoicesRepository;

    //@Scheduled(cron="0/10 * * * * *") //TODO 테스트 할 때에만 사용 나중에 삭제
    @Scheduled(cron = "@monthly")
    public void deleteExpiredUser(){
        log.info("영구적으로 탈퇴되었습니다.");

        List<User> users = userRepository.deleteExpiredUser();

        users.stream()
                .filter(user -> userRepository.findByIdAndParentchild(user.getId(), user.getParentchild()) != null)
                .flatMap(user -> userRepository.findByIdAndParentchild(user.getId(), user.getParentchild()).stream())
                .filter(u -> u.isDeleted())
                .forEach(u -> {
                    log.info("parentchild 삭제=" + u.getParentchild().getId());
                    // parentchild 삭제
                    parentchildRepository.deleteById(u.getParentchild().getId());

                    // 해당 parentchild에 속한 모든 user 삭제
                    List<User> usersToDelete = userRepository.findByParentchild(u.getParentchild());
                    deleteUser(usersToDelete);

                });
    }

    private void deleteUser(List<User> usersToDelete){
        for (User userToDelete : usersToDelete) {
            // user의 참조 끊기
            userToDelete.setParentchildToNull();
            userToDelete.setUserMissionToNull();

            log.info("탈퇴할 유저 닉네임=" + userToDelete.getNickname());
            healthRepository.deleteByUser(userToDelete);

            userMissionChoicesRepository.deleteByUser(userToDelete);
            userMissionRepository.deleteByUser(userToDelete);

            // user 삭제
            userRepository.deleteById(userToDelete.getId());
        }

    }
}
