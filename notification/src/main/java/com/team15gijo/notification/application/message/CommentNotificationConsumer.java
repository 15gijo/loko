package com.team15gijo.notification.application.message;

import com.team15gijo.notification.application.dto.v1.message.ChatNotificationEventDto;
import com.team15gijo.notification.application.dto.v1.message.CommentNotificationEventDto;
import com.team15gijo.notification.application.dto.v1.message.FollowNotificationEventDto;
import com.team15gijo.notification.application.service.v1.EmitterService;
import com.team15gijo.notification.domain.model.Notification;
import com.team15gijo.notification.domain.model.NotificationType;
import com.team15gijo.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentNotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final EmitterService emitterService;

    @KafkaListener(topics = "COMMENT", groupId = "notification-service", containerFactory = "commentKafkaListenerContainerFactory")
    @Transactional
    public void commentConsumer(CommentNotificationEventDto event) {
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
    }

    @KafkaListener(topics = "FOLLOW", groupId = "notification-service", containerFactory = "followKafkaListenerContainerFactory")
    @Transactional
    public void followConsumer(FollowNotificationEventDto event) {
        System.out.println("📩 받은 Kafka 메시지: " + event);
        log.info("📨 Kafka COMMENT 메시지 수신: {}", event);
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
    }

    @KafkaListener(topics = "CHAT", groupId = "notification-service", containerFactory = "chatKafkaListenerContainerFactory")
    @Transactional
    public void chatConsumer(ChatNotificationEventDto event) {
        System.out.println("📩 받은 Kafka 메시지: " + event);
        log.info("📨 Kafka COMMENT 메시지 수신: {}", event);
        Long receiverId = event.getReceiverId();
        String nickname = event.getNickname();
        String eventId = receiverId + "_" + System.currentTimeMillis() + "_chat";
        String content = String.format("%s님이 채팅을 보냈습니다.", nickname);


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
    }

}
