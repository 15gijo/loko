package com.team15gijo.user.infrastructure.redis.repository;

import java.util.Optional;

public interface OAuthSignUpRedisRespository<K, V> {

    Optional<V> get(K key);

    void delete(K key);
}
