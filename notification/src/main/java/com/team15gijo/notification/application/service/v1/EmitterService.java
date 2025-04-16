package com.team15gijo.notification.application.service.v1;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.notification.application.dto.v1.NotificationResponseDto;
import com.team15gijo.notification.domain.exception.NotificationDomainExceptionCode;
import com.team15gijo.notification.domain.model.NotificationStatus;
import com.team15gijo.notification.domain.model.NotificationType;
import com.team15gijo.notification.domain.repository.EmitterRepository;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmitterService {

    private static final Long TIMEOUT = 60L * 1000 * 60; // 60분
    private final EmitterRepository emitterRepository;
    private final RedisTemplate<String, String> redisTemplate;

//    public SseEmitter subscribe(Long userId) {
//        String emitterId = userId + "_" + System.currentTimeMillis();
//        SseEmitter emitter = new SseEmitter(TIMEOUT);
//        emitterRepository.save(emitterId, emitter);
//
//        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
//        emitter.onTimeout(() -> {
//            emitter.complete();
//            emitterRepository.deleteById(emitterId);
//        });
//
//        try {
//            emitter.send(SseEmitter.event()
//                    .id(emitterId)
//                    .name(NotificationStatus.CONNECT.name())
//                    .data("connected"));
//        } catch (IOException e) {
//            emitter.completeWithError(e);
//        }
//
//        return emitter;
//    }

//    public void send(Long receiverId, NotificationType type, String content, String eventId, UUID notificationId) {
//        log.info("📡 SSE 전송 대상 receiverId = {}, eventId = {}", receiverId, eventId);
//        NotificationResponseDto response = NotificationResponseDto.builder()
//                .notificationId(notificationId)
//                .notificationType(type)
//                .notificationContent(content)
//                .eventId(eventId)
//                .build();
//
//        Map<String, SseEmitter> emitters = emitterRepository.findAllByUserId(receiverId);
//        emitters.forEach((emitterId, emitter) -> {
//            try {
//                emitter.send(SseEmitter.event()
//                        .id(eventId)
//                        .name(NotificationStatus.NEW.name())
//                        .data(response));
//            } catch (IOException e) {
//                emitter.complete();
//                emitterRepository.deleteById(emitterId);
//            }
//        });
//    }

    public SseEmitter subscribe(Long userId) {
        // 기존 emitter 정리
        try {
            Set<String> oldEmitterIds = redisTemplate.opsForSet().members("sse:emitters:" + userId);
            if (!oldEmitterIds.isEmpty()) {
                for (String oldId : oldEmitterIds) {
                    SseEmitter oldEmitter = emitterRepository.get(oldId);
                    if (oldEmitter != null) oldEmitter.complete();
                    emitterRepository.delete(oldId);
                }
                redisTemplate.delete("sse:emitters:" + userId); // 기존 emitterId 모두 제거
            }

            String emitterId = userId + "_" + System.currentTimeMillis();
            SseEmitter emitter = new SseEmitter(TIMEOUT);
            emitterRepository.save(emitterId, emitter);
            // Redis에 emitterId 저장
            redisTemplate.opsForSet().add("sse:emitters:" + userId, emitterId);
            // TTL 설정
            redisTemplate.opsForValue()
                    .set("sse:ttl:" + emitterId, "", TIMEOUT, TimeUnit.MILLISECONDS);

            emitter.onCompletion(() -> {
                cleanupEmitter(userId, emitterId);
            });

            emitter.onTimeout(() -> {
                cleanupEmitter(userId, emitterId);
            });

            // 연결 확인용
            sendToClient(emitter, "CONNECT", "SSE 연결 완료");
            return emitter;
        } catch (Exception e) {
            log.error("❌ SSE 구독 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(NotificationDomainExceptionCode.SCRIBE_FAIL);
        }
    }

    public void send(Long receiverId, NotificationType type, String content, String eventId, UUID notificationId) {
        log.info("📡 SSE 전송 대상 receiverId = {}, eventId = {}", receiverId, eventId);
        Set<String> emitterIds = redisTemplate.opsForSet().members("sse:emitters:" + receiverId);
        if (emitterIds.isEmpty()) return;

        for (String emitterId : emitterIds) {
            SseEmitter emitter = emitterRepository.get(emitterId);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(eventId)
                            .name("NEW")
                            .data(NotificationResponseDto.builder()
                                    .notificationId(notificationId)
                                    .notificationContent(content)
                                    .notificationType(type)
                                    .isChecked(false)
                                    .build()
                            )
                    );
                } catch (IOException e) {
                    // 전송 중 클라이언트가 연결을 끊었거나, 네트워크 오류 등으로 문제가 생긴 경우
                    log.error("❌ 메세지 전송 중 오류 발생: {}", e.getMessage(), e);
                    emitter.complete();  // 연결 종료 처리
                    emitterRepository.delete(emitterId);   // 로컬에서 제거
                    redisTemplate.opsForSet().remove("sse:emitters:" + receiverId, emitterId);  // Redis에서 제거
                }
            } else {
                log.warn("emitterId가 존재하지 않습니다.");
            }
        }
    }


    private void sendToClient(SseEmitter emitter, String name, String data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(name)
                    .data(data));
        } catch (IOException e) {
            emitter.complete();
        }
    }

    private void cleanupEmitter(Long userId, String emitterId) {
        emitterRepository.delete(emitterId);
        redisTemplate.opsForSet().remove("sse:emitters:" + userId, emitterId);
        redisTemplate.delete("sse:ttl:" + emitterId); // TTL key도 제거
    }

}
