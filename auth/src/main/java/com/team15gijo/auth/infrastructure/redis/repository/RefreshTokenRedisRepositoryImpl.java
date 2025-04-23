package com.team15gijo.auth.infrastructure.redis.repository;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepositoryImpl implements RedisRepository<Long, String> {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "REFRESH_TOKEN:";


    @Override
    public void save(Long userId, String value, long ttl) {
        String key = PREFIX + userId;
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(ttl));
    }

    @Override
    public Optional<String> get(Long userId) {
        String key = PREFIX + userId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void delete(Long userId) {
        redisTemplate.delete(PREFIX + userId);
    }
}
