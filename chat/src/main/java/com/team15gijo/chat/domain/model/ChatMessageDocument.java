package com.team15gijo.chat.domain.model;

import com.team15gijo.chat.presentation.dto.v1.ChatMessageResponseDto;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document(collection = "chat")
public class ChatMessageDocument {
    @Id
    private String _id;
    private UUID chatRoomId;
    private String senderId;
    private ConnectionType connectionType;
    private String messageContent;
    private ChatMessageType chatMessageType;
    private LocalDateTime sentAt;
    private Boolean readStatus;

    public ChatMessageResponseDto toResponse() {
        return ChatMessageResponseDto.builder()
            .senderId(senderId)
            .message(messageContent)
            .sentAt(sentAt)
            .readStatus(readStatus).build();
    }
}
