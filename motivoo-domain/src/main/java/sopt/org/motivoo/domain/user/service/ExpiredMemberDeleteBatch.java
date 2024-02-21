package sopt.org.motivoo.domain.user.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.health.repository.HealthRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionChoicesRetriever;
import sopt.org.motivoo.domain.mission.repository.UserMissionRetriever;
import sopt.org.motivoo.domain.parentchild.repository.ParentchildRetriever;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.repository.UserRetriever;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ExpiredMemberDeleteBatch {

    private final UserRetriever userRetriever;
    private final ParentchildRetriever parentchildRetriever;
    private final HealthRetriever healthRetriever;
    private final UserMissionRetriever userMissionRetriever;
    private final UserMissionChoicesRetriever userMissionChoicesRetriever;

    //@Scheduled(cron="0/10 * * * * *") //TODO 테스트 할 때에만 사용 나중에 삭제
    @Scheduled(cron = "@monthly")
    public void deleteExpiredUser(){
        log.info("영구적으로 탈퇴되었습니다.");

        List<User> users = userRetriever.deleteExpiredUsers();

        users.stream()
                .filter(user -> userRetriever.getByIdAndParentchild(user.getId(), user.getParentchild()).isPresent())
                .flatMap(user -> userRetriever.getByIdAndParentchild(user.getId(), user.getParentchild()).stream())
                .filter(u -> u.isDeleted())
                .forEach(u -> {
                    log.info("parentchild 삭제=" + u.getParentchild().getId());

                    // parentchild 삭제
                    parentchildRetriever.deleteById(u.getParentchild().getId());

                    // 해당 parentchild에 속한 모든 user 삭제
                    List<User> usersToDelete = userRetriever.getAllByParentchild(u.getParentchild());
                    deleteUser(usersToDelete);

                });
    }


    private void deleteUser(List<User> usersToDelete){

        for (User userToDelete : usersToDelete) {

            // User의 참조 끊기
            userToDelete.setParentchildToNull();
            userToDelete.setUserMissionToNull();

            log.info("탈퇴할 유저 닉네임=" + userToDelete.getNickname());
            healthRetriever.deleteByUser(userToDelete);

            userMissionChoicesRetriever.deleteByUser(userToDelete);
            userMissionRetriever.deleteByUser(userToDelete);

            // user 삭제
            userRetriever.deleteById(userToDelete.getId());
        }

    }
}
