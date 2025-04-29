package com.team15gijo.follow.infrastructure.redis.repository;

import java.util.Optional;

public interface FollowRedisRepository<K> {

    void incrementFollowerCount(K followeeId);

    void decrementFollowerCount(K followeeId);

    void incrementFollowingCount(K followerId);

    void decrementFollowingCount(K followerId);

    Optional<Integer> getFollowerCount(K followeeId);

    Optional<Integer> getFollowingCount(K followerId);

    void saveFollowerCount(K s, int i);

    void saveFollowingCount(K s, int i);
}

