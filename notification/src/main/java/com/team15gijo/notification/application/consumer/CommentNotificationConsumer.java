package com.team15gijo.notification.application.consumer;

import com.team15gijo.notification.application.dto.v1.CommentNotificationEvent;
import com.team15gijo.notification.application.service.v1.EmitterService;
import com.team15gijo.notification.domain.model.Notification;
import com.team15gijo.notification.domain.model.NotificationType;
import com.team15gijo.notification.domain.repository.NotificationRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentNotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final EmitterService emitterService;

    @KafkaListener(topics = "COMMENT", groupId = "notification-service")
    public void commentConsumer(CommentNotificationEvent event) {

        Long receiverId = event.getReceiverId();
        String nickname = event.getNickname();
        String comment = event.getCommentContent();
        String eventId = receiverId + "_" + System.currentTimeMillis();
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

}
