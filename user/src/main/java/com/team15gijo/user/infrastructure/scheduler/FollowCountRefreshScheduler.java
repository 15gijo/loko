package com.team15gijo.user.infrastructure.scheduler;

import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.infrastructure.redis.repository.UserRedisRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowCountRefreshScheduler {

    private final UserApplicationService userApplicationService;
    private final UserRedisRepository<String> userRedisRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String FOLLOWER_PREFIX = "followee:followerCount:";
    private static final String FOLLOWING_PREFIX = "follower:followingCount:";

    @Scheduled(fixedDelay = 10 * 60 * 1000) // 10분마다
    public void refreshFollowCounts() {
        log.info("[Batch] Redis followerCount/followingCount 리프레시 시작");

        List<UserEntity> activeUsers = userApplicationService.getAllUsers();

        log.info("[Batch] Redis 리프레시 유저 ID 목록={}", activeUsers.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList()));
        for (UserEntity user : activeUsers) {
            String userId = String.valueOf(user.getId());

            // followerCount > 0이고 Redis에 키 없으면 저장
            if (user.getFollowerCount() > 0 &&
                    !Boolean.TRUE.equals(redisTemplate.hasKey(FOLLOWER_PREFIX + userId))) {
                userRedisRepository.saveFollowerCount(userId, user.getFollowerCount());
            }

            if (user.getFollowingCount() > 0 &&
                    !Boolean.TRUE.equals(redisTemplate.hasKey(FOLLOWING_PREFIX + userId))) {
                userRedisRepository.saveFollowingCount(userId, user.getFollowingCount());
            }
        }

        log.info("[Batch] Redis followerCount/followingCount 리프레시 완료");
    }
}
