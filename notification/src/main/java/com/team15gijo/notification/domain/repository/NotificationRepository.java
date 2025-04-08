package com.team15gijo.notification.domain.repository;

import com.team15gijo.notification.domain.model.Notification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

}
