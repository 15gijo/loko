package com.team15gijo.notification.domain.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


public interface EmitterRepository {
    void save(String emitterId, SseEmitter emitter);
    SseEmitter get(String emitterId);
    void delete(String emitterId);
    Map<String, SseEmitter> findAllStartWithByUserId(Long userId);
    void deleteAllStartWithByUserId(Long userId);
}
