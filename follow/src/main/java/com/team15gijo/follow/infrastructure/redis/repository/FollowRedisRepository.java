package com.team15gijo.follow.infrastructure.redis.repository;

import java.util.Optional;
import java.util.function.Supplier;

public interface FollowRedisRepository<K> {

    void incrementFollowerCount(K followeeId);

    void decrementFollowerCount(K followeeId);

    void incrementFollowingCount(K followerId);

    void decrementFollowingCount(K followerId);

    Optional<Long> getFollowerCount(K followeeId);

    Optional<Long> getFollowingCount(K followerId);

    void saveFollowerCount(K userId, long i);

    void saveFollowingCount(K userId, long i);

    Optional<Long> getFollowerCountWithFallback(K followeeId, Supplier<Long> fallback);

    Optional<Long> getFollowingCountWithFallback(K followerId, Supplier<Long> fallback);
}

