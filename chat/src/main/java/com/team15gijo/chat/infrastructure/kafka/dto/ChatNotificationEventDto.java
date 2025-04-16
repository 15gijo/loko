package com.team15gijo.chat.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatNotificationEventDto {
    private Long receiverId;         // 알림 받을 사용자 ID
    private String nickname;           // 알림을 보내는 사용자의 닉네임
    private String messageContent;   // 채팅 내용

    public static ChatNotificationEventDto from(Long receiverId, String nickname, String messageContent) {
        return ChatNotificationEventDto.builder()
                .receiverId(receiverId)
                .nickname(nickname)
                .messageContent(messageContent)
                .build();
    }
}
