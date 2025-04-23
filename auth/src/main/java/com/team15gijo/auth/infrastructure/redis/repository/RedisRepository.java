package com.team15gijo.auth.infrastructure.redis.repository;

import java.util.Optional;

public interface RedisRepository<K, V> {

    void save(K key, V value, long ttl);

    Optional<V> get(K key);

    void delete(K key);
}
