package com.team15gijo.notification.application.service.v1;

import com.team15gijo.notification.application.dto.v1.NotificationResponseDto;
import com.team15gijo.notification.domain.model.NotificationStatus;
import com.team15gijo.notification.domain.model.NotificationType;
import com.team15gijo.notification.domain.repository.EmitterRepository;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmitterService {

    private static final Long TIMEOUT = 60L * 1000 * 60; // 60분
    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(Long userId) {
        String emitterId = userId + "_" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterRepository.save(emitterId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitterRepository.deleteById(emitterId);
        });

        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name(NotificationStatus.CONNECT.name())
                    .data("connected"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void send(Long receiverId, NotificationType type, String content, String eventId, UUID notificationId) {
        log.info("📡 SSE 전송 대상 receiverId = {}, eventId = {}", receiverId, eventId);
        NotificationResponseDto response = NotificationResponseDto.builder()
                .notificationId(notificationId)
                .notificationType(type)
                .notificationContent(content)
                .eventId(eventId)
                .build();

        Map<String, SseEmitter> emitters = emitterRepository.findAllByUserId(receiverId);
        emitters.forEach((emitterId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(eventId)
                        .name(NotificationStatus.NEW.name())
                        .data(response));
            } catch (IOException e) {
                emitter.complete();
                emitterRepository.deleteById(emitterId);
            }
        });
    }

}
