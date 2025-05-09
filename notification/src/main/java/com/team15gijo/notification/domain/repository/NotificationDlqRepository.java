package com.team15gijo.notification.domain.repository;

import com.team15gijo.notification.domain.model.NotificationDlq;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationDlqRepository extends JpaRepository<NotificationDlq, Long> {

    List<NotificationDlq> findByResolvedFalse();

}
