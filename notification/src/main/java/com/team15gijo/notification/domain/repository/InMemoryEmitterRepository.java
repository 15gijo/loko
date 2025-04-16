package com.team15gijo.notification.domain.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class InMemoryEmitterRepository implements EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void save(String emitterId, SseEmitter emitter) {
        emitters.put(emitterId, emitter);
    }

    @Override
    public SseEmitter get(String emitterId) {
        return emitters.get(emitterId);
    }

    @Override
    public void delete(String emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public Map<String, SseEmitter> findAllStartWithByUserId(Long userId) {
        String prefix = userId + "_";
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteAllStartWithByUserId(Long userId) {
        String prefix = userId + "_";
        emitters.keySet().removeIf(key -> key.startsWith(prefix));
    }
}
