package com.team15gijo.notification.infrastructure.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.common.exception.CustomException;
import com.team15gijo.notification.application.service.v1.EmitterService;
import com.team15gijo.notification.domain.exception.NotificationDomainExceptionCode;
import com.team15gijo.notification.domain.model.Notification;
import com.team15gijo.notification.domain.model.NotificationDlq;
import com.team15gijo.notification.domain.model.NotificationType;
import com.team15gijo.notification.domain.repository.NotificationDlqRepository;
import com.team15gijo.notification.domain.repository.NotificationRepository;
import com.team15gijo.notification.infrastructure.kafka.dto.ChatNotificationEventDto;
import com.team15gijo.notification.infrastructure.kafka.dto.CommentNotificationEventDto;
import com.team15gijo.notification.infrastructure.kafka.dto.FollowNotificationEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumerService {

    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final EmitterService emitterService;
    private final NotificationDlqRepository dlqRepository;

    @KafkaListener(topics = "COMMENT", groupId = "notification-service", containerFactory = "commentKafkaListenerContainerFactory")
    @Transactional
    @RetryableTopic(attempts = "3",
            backoff = @Backoff(delay = 1000, maxDelay = 3000, random = true), // 1~3초 랜덤 재시도
            dltStrategy = DltStrategy.FAIL_ON_ERROR, // 재시도 실패 후 DLQ 이동
            dltTopicSuffix = "-dlt", // DLQ 토픽 접미사
            exclude = { IllegalArgumentException.class, NullPointerException.class }
    )
    public void commentConsumer(CommentNotificationEventDto event) {
        try {
            System.out.println("📩 받은 Kafka 메시지: " + event);
            log.info("📨 Kafka COMMENT 메시지 수신: {}", event);
            Long receiverId = event.getReceiverId();
            String nickname = event.getNickname();
            String comment = event.getCommentContent();
            String eventId = receiverId + "_" + System.currentTimeMillis() + "_comment";
            String content = String.format("%s님이 '%s' 댓글을 작성하였습니다.", nickname, comment);


            // 1. 알림 DB 저장
            Notification notification = Notification.createNotification(receiverId, NotificationType.COMMENT, content, eventId);

            notificationRepository.save(notification);

            // 2. SSE로 사용자에게 전송
            emitterService.send(
                    receiverId,
                    NotificationType.COMMENT,
                    content,
                    eventId,
                    notification.getNotificationId()
            );
        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 중 예외 발생", e);
            throw e;
        }
    }

    @KafkaListener(topics = "COMMENT-dlt", groupId = "notification-service", containerFactory = "commentKafkaListenerContainerFactory")
    public void handleCommentEventDlt(CommentNotificationEventDto event) {
        log.error("🔥 DLQ로 이동된 메시지 수신: {}", event);
        // 여기서 Slack 알림 보내거나 Kibana/DB 저장 추가
        try {
            String payload = objectMapper.writeValueAsString(event);
            NotificationDlq dlq = NotificationDlq.builder()
                    .type("COMMENT")
                    .payload(payload)
                    .errorMessage("COMMENT DLT 수신")
                    .resolved(false)
                    .build();
            dlqRepository.save(dlq);
        } catch (Exception e) {
            log.error("DLQ 저장 실패", e);
            throw new CustomException(NotificationDomainExceptionCode.DLT_SAVE_FAIL);
        }
    }


    @KafkaListener(topics = "FOLLOW", groupId = "notification-service", containerFactory = "followKafkaListenerContainerFactory")
    @Transactional
    @RetryableTopic(attempts = "3",
            backoff = @Backoff(delay = 1000, maxDelay = 3000, random = true), // 1~3초 랜덤 재시도
            dltStrategy = DltStrategy.FAIL_ON_ERROR, // 재시도 실패 후 DLQ 이동
            dltTopicSuffix = "-dlt", // DLQ 토픽 접미사
            exclude = { IllegalArgumentException.class, NullPointerException.class }
    )
    public void followConsumer(FollowNotificationEventDto event) {
        try {
            log.info("📨 Kafka FOLLOW 메시지 수신: {}", event);
            Long receiverId = event.getReceiverId();
            String nickname = event.getNickname();
            String eventId = receiverId + "_" + System.currentTimeMillis() + "_follow";
            String content = String.format("%s님이 당신을 팔로우하였습니다.", nickname);


            // 1. 알림 DB 저장
            Notification notification = Notification.createNotification(receiverId, NotificationType.FOLLOW, content, eventId);

            notificationRepository.save(notification);

            // 2. SSE로 사용자에게 전송
            emitterService.send(
                    receiverId,
                    NotificationType.FOLLOW,
                    content,
                    eventId,
                    notification.getNotificationId()
            );
        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 중 예외 발생", e);
            throw e;
        }
    }

    @KafkaListener(topics = "FOLLOW-dlt", groupId = "notification-service", containerFactory = "followKafkaListenerContainerFactory")
    public void handleFollowEventDlt(FollowNotificationEventDto event) {
        log.error("🔥 DLQ로 이동된 메시지 수신: {}", event);
        try {
            String payload = objectMapper.writeValueAsString(event);
            NotificationDlq dlq = NotificationDlq.builder()
                    .type("FOLLOW")
                    .payload(payload)
                    .errorMessage("FOLLOW DLT 수신")
                    .resolved(false)
                    .build();
            dlqRepository.save(dlq);
        } catch (Exception e) {
            log.error("DLQ 저장 실패", e);
            throw new CustomException(NotificationDomainExceptionCode.DLT_SAVE_FAIL);
        }
    }


    @KafkaListener(topics = "CHAT", groupId = "notification-service", containerFactory = "chatKafkaListenerContainerFactory")
    @Transactional
    @RetryableTopic(attempts = "3",
            backoff = @Backoff(delay = 1000, maxDelay = 3000, random = true), // 1~3초 랜덤 재시도
            dltStrategy = DltStrategy.FAIL_ON_ERROR, // 재시도 실패 후 DLQ 이동
            dltTopicSuffix = "-dlt", // DLQ 토픽 접미사
            exclude = { IllegalArgumentException.class, NullPointerException.class }
    )
    public void chatConsumer(ChatNotificationEventDto event) {
        try {
            System.out.println("📩 받은 Kafka 메시지: " + event);
            log.info("📨 Kafka CHAT 메시지 수신: {}", event);
            Long receiverId = event.getReceiverId();
            String nickname = event.getNickname();
            String messageContent = event.getMessageContent();
            String eventId = receiverId + "_" + System.currentTimeMillis() + "_chat";
            String content = String.format("%s : [%s]", nickname, messageContent);


            // 1. 알림 DB 저장
            Notification notification = Notification.createNotification(receiverId, NotificationType.CHAT, content, eventId);

            notificationRepository.save(notification);

            // 2. SSE로 사용자에게 전송
            emitterService.send(
                    receiverId,
                    NotificationType.CHAT,
                    content,
                    eventId,
                    notification.getNotificationId()
            );
        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 중 예외 발생", e);
            throw e;
        }
    }

    @KafkaListener(topics = "CHAT-dlt", groupId = "notification-service", containerFactory = "chatKafkaListenerContainerFactory")
    public void handleChatEventDlt(ChatNotificationEventDto event) {
        log.error("🔥 DLQ로 이동된 메시지 수신: {}", event);
        try {
            String payload = objectMapper.writeValueAsString(event);
            NotificationDlq dlq = NotificationDlq.builder()
                    .type("CHAT")
                    .payload(payload)
                    .errorMessage("CHAT DLT 수신")
                    .resolved(false)
                    .build();
            dlqRepository.save(dlq);
        } catch (Exception e) {
            log.error("DLQ 저장 실패", e);
            throw new CustomException(NotificationDomainExceptionCode.DLT_SAVE_FAIL);
        }
    }

}
