package com.team15gijo.notification.domain.model;

import com.team15gijo.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "p_notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_notification SET is_deleted = true WHERE notification_id = ?")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private UUID notificationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "notification_content", nullable = false, length = 50)
    private String notificationContent;

    @Column(name = "receiver", nullable = false)
    private Long receiver;

    @Column(name = "is_check", nullable = false)
    private Boolean isChecked;

    @Column(name = "event_id")
    private String eventId;     // SSE 이벤트 고유 식별자. 클라이언트 reconnect 및 이벤트 트래킹/재전송용


}
