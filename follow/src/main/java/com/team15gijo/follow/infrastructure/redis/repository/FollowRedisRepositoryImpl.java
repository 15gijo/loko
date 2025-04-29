package com.team15gijo.follow.infrastructure.redis.repository;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowRedisRepositoryImpl implements FollowRedisRepository<String> {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String FOLLOWER_PREFIX = "followee:followerCount:";
    private static final String FOLLOWING_PREFIX = "follower:followingCount:";

    private static final long TTL_SECONDS = 3 * 24 * 60 * 60; //3일

    private String makeFollowerKey(String userId) {
        return FOLLOWER_PREFIX + userId;
    }

    private String makeFollowingKey(String userId) {
        return FOLLOWING_PREFIX + userId;
    }

    @Override
    public void incrementFollowerCount(String userId) {
        String key = makeFollowerKey(userId);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        Long value = redisTemplate.opsForValue().increment(key, 1);
        if (!exists && value != null && value == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(TTL_SECONDS));
        }
    }

    @Override
    public void decrementFollowerCount(String userId) {
        String key = makeFollowerKey(userId);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (exists) {
            redisTemplate.opsForValue().decrement(key, 1);
        }
    }

    @Override
    public void incrementFollowingCount(String userId) {
        String key = makeFollowingKey(userId);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        Long value = redisTemplate.opsForValue().increment(key, 1);
        if (!exists && value != null && value == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(TTL_SECONDS));
        }
    }

    @Override
    public void decrementFollowingCount(String userId) {
        String key = makeFollowingKey(userId);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (exists) {
            redisTemplate.opsForValue().decrement(key, 1);
        }
    }

    @Override
    public Optional<Integer> getFollowerCount(String followeeId) {
        String value = redisTemplate.opsForValue().get(makeFollowerKey(followeeId));
        return Optional.ofNullable(value).map(Integer::parseInt);
    }

    @Override
    public Optional<Integer> getFollowingCount(String followerId) {
        String value = redisTemplate.opsForValue().get(makeFollowingKey(followerId));
        return Optional.ofNullable(value).map(Integer::parseInt);
    }

    @Override
    public void saveFollowerCount(String userId, int count) {
        String key = makeFollowerKey(userId);
        redisTemplate.opsForValue().set(key, String.valueOf(count), Duration.ofSeconds(TTL_SECONDS));
    }

    @Override
    public void saveFollowingCount(String userId, int count) {
        String key = makeFollowingKey(userId);
        redisTemplate.opsForValue().set(key, String.valueOf(count), Duration.ofSeconds(TTL_SECONDS));
    }

}
