package sopt.org.motivooServer.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.motivooServer.domain.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class ExpiredMemberDeleteBatch {
    private final UserRepository userRepository;

    @Scheduled(cron = "@monthly")
    public void deleteExpiredUser(){
        userRepository.deleteExpiredUser();
    }
}
