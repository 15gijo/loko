package com.team15gijo.auth.infrastructure.redis.repository;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthOAuthSignUpRedisRepositoryImpl implements RedisRepository<String, String>{

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "OAUTH:SIGNUP:";

    @Override
    public void save(String key, String value, long ttl) {
        String redisKey = PREFIX + key;
        redisTemplate.opsForValue().set(redisKey, value, Duration.ofMillis(ttl));
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.empty();
    }

    @Override
    public void delete(String key) {

    }
}
