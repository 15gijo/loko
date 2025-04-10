package com.team15gijo.notification.domain.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {

    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public void save(String emitterId, SseEmitter emitter) {
        emitterMap.put(emitterId, emitter);
    }

    public Map<String, SseEmitter> findAllByUserId(Long userId) {
        String key = userId.toString();
        return emitterMap.entrySet().stream()
                .filter(e -> e.getKey().startsWith(key))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteById(String emitterId) {
        emitterMap.remove(emitterId);
    }

}
