package com.team15gijo.user.infrastructure.redis.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRedisRepositoryImpl implements UserRedisRepository<String> {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String FOLLOWER_PREFIX = "followee:followerCount:";
    private static final String FOLLOWING_PREFIX = "follower:followingCount:";
    private static final long TTL_SECONDS = 3 * 24 * 60 * 60; // 3일

    @Override
    public void saveFollowerCount(String userId, int count) {
        String key = FOLLOWER_PREFIX + userId;
        redisTemplate.opsForValue()
                .set(key, String.valueOf(count), Duration.ofSeconds(TTL_SECONDS));
    }

    @Override
    public void saveFollowingCount(String userId, int count) {
        String key = FOLLOWING_PREFIX + userId;
        redisTemplate.opsForValue()
                .set(key, String.valueOf(count), Duration.ofSeconds(TTL_SECONDS));
    }
}
