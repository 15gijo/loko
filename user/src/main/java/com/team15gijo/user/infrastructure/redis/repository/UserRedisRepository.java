package com.team15gijo.user.infrastructure.redis.repository;

public interface UserRedisRepository<K> {

    void saveFollowerCount(K userId, int count);

    void saveFollowingCount(K userId, int count);
}


