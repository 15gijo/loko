package com.team15gijo.user.infrastructure.redis.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserOAuthSignUpRedisRepositoryImpl implements OAuthSignUpRedisRespository<String, String>{

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "OAUTH:SIGNUP:";

    @Override
    public Optional<String> get(String key) {
        String redisKey = PREFIX + key;
        return Optional.ofNullable(redisTemplate.opsForValue().get(redisKey));
    }

    @Override
    public void delete(String key) {
        String redisKey = PREFIX + key;
        redisTemplate.delete(redisKey);
    }
}
