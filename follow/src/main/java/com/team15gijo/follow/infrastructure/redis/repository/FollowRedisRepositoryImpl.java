package com.team15gijo.follow.infrastructure.redis.repository;

import com.team15gijo.follow.infrastructure.redis.lock.RedissonLockExecutor;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FollowRedisRepositoryImpl implements FollowRedisRepository<String> {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonLockExecutor redissonLockExecutor;

    private static final String FOLLOWER_PREFIX = "followee:followerCount:";
    private static final String FOLLOWING_PREFIX = "follower:followingCount:";
    private static final String FOLLOWER_LOCK_PREFIX = "lock:follower:";
    private static final String FOLLOWING_LOCK_PREFIX = "lock:following:";

    private static final long TTL_SECONDS = 3 * 24 * 60 * 60; //3일

    private String makeFollowerKey(String userId) {
        return FOLLOWER_PREFIX + userId;
    }

    private String makeFollowingKey(String userId) {
        return FOLLOWING_PREFIX + userId;
    }

    private String makeFollowerLockKey(String userId) {
        return FOLLOWER_LOCK_PREFIX + userId;
    }

    private String makeFollowingLockKey(String userId) {
        return FOLLOWING_LOCK_PREFIX + userId;
    }

    @Override
    public void incrementFollowerCount(String userId) {
        String key = makeFollowerKey(userId);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        Long value = redisTemplate.opsForValue().increment(key, 1L);
        if (!exists && value != null && value == 1L) {
            redisTemplate.expire(key, Duration.ofSeconds(TTL_SECONDS));
        }
    }

    @Override
    public void decrementFollowerCount(String userId) {
        String key = makeFollowerKey(userId);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (exists) {
            redisTemplate.opsForValue().decrement(key, 1L);
        }
    }

    @Override
    public void incrementFollowingCount(String userId) {
        String key = makeFollowingKey(userId);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        Long value = redisTemplate.opsForValue().increment(key, 1L);
        if (!exists && value != null && value == 1L) {
            redisTemplate.expire(key, Duration.ofSeconds(TTL_SECONDS));
        }
    }

    @Override
    public void decrementFollowingCount(String userId) {
        String key = makeFollowingKey(userId);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (exists) {
            redisTemplate.opsForValue().decrement(key, 1L);
        }
    }

    @Override
    public Optional<Long> getFollowerCount(String followeeId) {
        String value = redisTemplate.opsForValue().get(makeFollowerKey(followeeId));
        return Optional.ofNullable(value).map(Long::parseLong);
    }

    @Override
    public Optional<Long> getFollowingCount(String followerId) {
        String value = redisTemplate.opsForValue().get(makeFollowingKey(followerId));
        return Optional.ofNullable(value).map(Long::parseLong);
    }

    @Override
    public void saveFollowerCount(String userId, long count) {
        String key = makeFollowerKey(userId);
        long ttlSeconds = (count == 0) ? 60 * 60 : TTL_SECONDS; // 1시간 or 3일
        redisTemplate.opsForValue()
                .set(key, String.valueOf(count), Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public void saveFollowingCount(String userId, long count) {
        String key = makeFollowingKey(userId);
        long ttlSeconds = (count == 0) ? 60 * 60 : TTL_SECONDS; // 1시간 or 3일
        redisTemplate.opsForValue()
                .set(key, String.valueOf(count), Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public Optional<Long> getFollowerCountWithFallback(String userId, Supplier<Long> dbFallback) {
        return redissonLockExecutor.executeWithLock(
                makeFollowerLockKey(userId),
                () -> getFollowerCount(userId),
                () -> {
                    Long dbCount = dbFallback.get();
                    log.info("[fallback-check] followerCount DB 조회 결과 - userId={}, dbCount={}",
                            userId, dbCount);
                    saveFollowerCount(userId, dbCount);
                    return dbCount;
                }
        );
    }

    @Override
    public Optional<Long> getFollowingCountWithFallback(String userId, Supplier<Long> dbFallback) {
        return redissonLockExecutor.executeWithLock(
                makeFollowingLockKey(userId),
                () -> getFollowingCount(userId),
                () -> {
                    Long dbCount = dbFallback.get();
                    log.info("[fallback-check] followingCount DB 조회 결과 - userId={}, dbCount={}",
                            userId, dbCount);
                    saveFollowingCount(userId, dbCount);
                    return dbCount;
                }
        );
    }

}
