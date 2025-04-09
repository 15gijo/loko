package com.team15gijo.notification.application.dto.v1;

import com.team15gijo.notification.domain.model.Notification;
import com.team15gijo.notification.domain.model.NotificationType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    private UUID notificationId;
    private NotificationType notificationType;
    private String notificationContent;
    private String eventId;

    public static NotificationResponseDto fromEntity(Notification notification) {
        return NotificationResponseDto.builder()
                .notificationId(notification.getNotificationId())
                .notificationType(notification.getNotificationType())
                .notificationContent(notification.getNotificationContent())
                .eventId(notification.getEventId())
                .build();
    }
}
