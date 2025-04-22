package com.team15gijo.chat.application.dto.v1;

import com.team15gijo.chat.domain.model.v1.ConnectionType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDto {
    private String id;
    private UUID chatRoomId;
    private Long senderId;
    private Long receiverId;
    private String receiverNickname;
    private ConnectionType connectionType;
    private String messageContent;
    private LocalDateTime sentAt;
    private Boolean readStatus;
    private LocalDateTime deletedAt;
    private Long deletedBy;
}
