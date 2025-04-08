package com.team15gijo.notification.application.service.v1;

import com.team15gijo.notification.application.dto.v1.NotificationResponseDto;
import com.team15gijo.notification.domain.model.Notification;
import com.team15gijo.notification.domain.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDto> getUnreadNotifications(Long userId) {
        return notificationRepository.findByReceiverAndIsCheckedFalse(userId).stream()
                .map(NotificationResponseDto::fromEntity)
                .toList();
    }

    @Transactional
    public void updateIsChecked(UUID notificationId, Long userId) {
        Notification notification = notificationRepository.findByNotificationIdAndReceiver(notificationId, userId)
                .orElseThrow(() -> new RuntimeException("알림이 존재하지 않습니다."));
        notification.updateIsChecked();
    }

}
