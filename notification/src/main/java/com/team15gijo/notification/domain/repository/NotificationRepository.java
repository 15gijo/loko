package com.team15gijo.notification.domain.repository;

import com.team15gijo.notification.domain.model.Notification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByReceiverAndIsCheckedFalse(Long receiverId);
    Optional<Notification> findByNotificationIdAndReceiver(UUID notificationId, Long receiverId);
}
