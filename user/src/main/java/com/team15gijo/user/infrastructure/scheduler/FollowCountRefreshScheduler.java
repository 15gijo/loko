package com.team15gijo.user.infrastructure.scheduler;

import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.infrastructure.redis.repository.UserRedisRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowCountRefreshScheduler {

    private final UserApplicationService userApplicationService;
    private final UserRedisRepository<String> userRedisRepository;

    @Scheduled(fixedDelay = 10 * 60 * 1000) // 10분마다
    public void refreshFollowCounts() {
        log.info("[Batch] Redis followerCount/followingCount 리프레시 시작");

        List<UserEntity> activeUsers = userApplicationService.getAllUsers();

        for (UserEntity user : activeUsers) {
            userRedisRepository.saveFollowerCount(String.valueOf(user.getId()), user.getFollowerCount());
            userRedisRepository.saveFollowingCount(String.valueOf(user.getId()), user.getFollowingCount());
        }

        log.info("[Batch] Redis followerCount/followingCount 리프레시 완료");
    }
}
