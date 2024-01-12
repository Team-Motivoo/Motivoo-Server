package sopt.org.motivooServer.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.motivooServer.domain.user.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ExpiredMemberDeleteBatch {
    private final UserRepository userRepository;

    //@Scheduled(cron="0/10 * * * * *")
    //@Scheduled(cron = "@hourly")
    public void deleteExpiredUser(){
        log.info("영구적으로 탈퇴되었습니다.");
        userRepository.deleteExpiredUser();
    }
}
