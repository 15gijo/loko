package com.team15gijo.auth.infrastructure.redis.repository;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlacklistRedisRepositoryImpl implements RedisRepository<String, String> {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "BLACKLIST:";

    @Override
    public void save(String accessToken, String value, long ttl) {
        String accessTokenKey = PREFIX + accessToken;
        redisTemplate.opsForValue().set(accessTokenKey, value, Duration.ofMillis(ttl));
    }

    @Override
    public Optional<String> get(String accessToken) {
        String accessTokenKey = PREFIX + accessToken;
        return Optional.ofNullable(redisTemplate.opsForValue().get(accessTokenKey));
    }

    @Override
    public void delete(String accessToken) {
        String accessTokenKey = PREFIX + accessToken;
        redisTemplate.delete(accessTokenKey);
    }
}
