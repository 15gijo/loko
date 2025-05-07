package com.team15gijo.notification.application.service.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.common.exception.CustomException;
import com.team15gijo.notification.domain.exception.NotificationDomainExceptionCode;
import com.team15gijo.notification.domain.model.Notification;
import com.team15gijo.notification.domain.model.NotificationDlq;
import com.team15gijo.notification.domain.model.NotificationType;
import com.team15gijo.notification.domain.repository.NotificationDlqRepository;
import com.team15gijo.notification.domain.repository.NotificationRepository;
import com.team15gijo.notification.infrastructure.kafka.dto.ChatNotificationEventDto;
import com.team15gijo.notification.infrastructure.kafka.dto.CommentNotificationEventDto;
import com.team15gijo.notification.infrastructure.kafka.dto.FollowNotificationEventDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final ObjectMapper objectMapper;
    private final NotificationDlqRepository dlqRepository;
    private final NotificationRepository notificationRepository;

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    @Transactional
    public void retryDlqMessages() {
        log.info("DLT 데이터 알림 변환 스케줄러 동작");
        List<NotificationDlq> failedMessages = dlqRepository.findByResolvedFalse();
        log.info("DLT 데이터 Size : {}", failedMessages.size());

        for (NotificationDlq dlq : failedMessages) {
            try {
                if ("FOLLOW".equals(dlq.getType())) {
                    FollowNotificationEventDto dto = objectMapper.readValue(dlq.getPayload(), FollowNotificationEventDto.class);
                    Long receiverId = dto.getReceiverId();
                    String nickname = dto.getNickname();
                    String eventId = receiverId + "_" + System.currentTimeMillis() + "_follow";
                    String content = String.format("%s님이 당신을 팔로우하였습니다.", nickname);

                    Notification notification = Notification.createNotification(receiverId, NotificationType.FOLLOW, content, eventId);
                    notificationRepository.save(notification);

                } else if ("COMMENT".equals(dlq.getType())) {
                    CommentNotificationEventDto dto = objectMapper.readValue(dlq.getPayload(), CommentNotificationEventDto.class);
                    Long receiverId = dto.getReceiverId();
                    String nickname = dto.getNickname();
                    String comment = dto.getCommentContent();
                    String eventId = receiverId + "_" + System.currentTimeMillis() + "_comment";
                    String content = String.format("%s님이 '%s' 댓글을 작성하였습니다.", nickname, comment);

                    Notification notification = Notification.createNotification(receiverId, NotificationType.COMMENT, content, eventId);
                    notificationRepository.save(notification);

                } else if ("CHAT".equals(dlq.getType())) {
                    ChatNotificationEventDto dto = objectMapper.readValue(dlq.getPayload(), ChatNotificationEventDto.class);
                    Long receiverId = dto.getReceiverId();
                    String nickname = dto.getNickname();
                    String messageContent = dto.getMessageContent();
                    String eventId = receiverId + "_" + System.currentTimeMillis() + "_chat";
                    String content = String.format("%s : [%s]", nickname, messageContent);

                    Notification notification = Notification.createNotification(receiverId, NotificationType.CHAT, content, eventId);
                    notificationRepository.save(notification);
                }
                dlq.setResolved(true);
                dlqRepository.save(dlq);
            } catch (Exception e) {
                log.info("알림 스케줄러 처리 중 알림 저장 실패 id={}, type={}, reason={}", dlq.getId(), dlq.getType(), e.getMessage(), e);
            }
        }
    }

}
