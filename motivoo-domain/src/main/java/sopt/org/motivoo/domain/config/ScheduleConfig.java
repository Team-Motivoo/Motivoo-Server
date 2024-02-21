package sopt.org.motivoo.domain.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * - 매일 자정마다 이루어지는 오늘의 미션 관련 작업
 * - 탈퇴한 회원을 관리하기 위한 Soft Delete
 * - 30일이 지난 운동 인증사진은 S3 Bucket에서 삭제
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {

    private static final int POOL_SIZE = 10;
    private static ThreadPoolTaskScheduler scheduler;


	@Bean
	public TaskScheduler scheduler() {
		scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(POOL_SIZE);
		scheduler.setThreadNamePrefix("현재 쓰레드 풀-");
		scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		scheduler.initialize();
		return scheduler;
	}
}
