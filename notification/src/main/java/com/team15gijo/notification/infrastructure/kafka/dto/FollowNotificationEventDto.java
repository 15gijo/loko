package com.team15gijo.notification.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowNotificationEventDto {
    private Long receiverId;         // 알림 받을 사용자 ID
    private String nickname;           // 알림을 보내는 사용자의 닉네임
}
