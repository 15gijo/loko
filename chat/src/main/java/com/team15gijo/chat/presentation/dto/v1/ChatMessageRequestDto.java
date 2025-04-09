package com.team15gijo.chat.presentation.dto.v1;

import com.team15gijo.chat.domain.model.ChatMessageType;
import com.team15gijo.chat.domain.model.ConnectionType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ChatMessageRequestDto {
    private String _id;
    private UUID chatRoomId;
    private String senderId;
    private ConnectionType connectionType;
    private String message;
    private ChatMessageType chatMessageType;
    private LocalDateTime sentAt;
    private Boolean readStatus;
}
