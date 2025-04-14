package com.team15gijo.notification.application.service.v1;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.notification.application.dto.v1.NotificationResponseDto;
import com.team15gijo.notification.domain.exception.NotificationDomainExceptionCode;
import com.team15gijo.notification.domain.model.Notification;
import com.team15gijo.notification.domain.repository.NotificationRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "알림")
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUnreadNotifications(Long userId) {
        try {
            List<Notification> notifications = notificationRepository.findByReceiverAndIsCheckedFalse(userId);

            if (notifications.isEmpty()) {
                log.info("✅ [{}] 사용자는 읽지 않은 알림이 없습니다.", userId);
                return Collections.emptyList();
            }

            return notifications.stream()
                    .map(NotificationResponseDto::fromEntity)
                    .toList();

        } catch (Exception e) {
            log.error("❌ 알림 조회 중 오류 발생 (userId: {}): {}", userId, e.getMessage(), e);
            throw new CustomException(NotificationDomainExceptionCode.DATABASE_ERROR);
        }
    }

    @Transactional
    public void updateIsChecked(UUID notificationId, Long userId) {
        Notification notification = notificationRepository.findByNotificationIdAndReceiver(notificationId, userId)
                .orElseThrow(() -> new CustomException(NotificationDomainExceptionCode.NOT_EXIST));
        notification.updateIsChecked();
    }

}
