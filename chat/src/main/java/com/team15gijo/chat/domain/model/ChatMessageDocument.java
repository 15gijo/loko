package com.team15gijo.chat.domain.model;

import com.team15gijo.chat.presentation.dto.v1.ChatMessageResponseDto;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "chat")
public class ChatMessageDocument {
    @Id
    private String _id;
    private UUID chatRoomId;
    private Long senderId;
    private String senderNickname;
    private ConnectionType connectionType;
    private String messageContent;
    private ChatMessageType chatMessageType;
    private LocalDateTime sentAt;
    private Boolean readStatus;

    public ChatMessageResponseDto toResponse() {
        return ChatMessageResponseDto.builder()
            .id(_id)
            .senderId(senderId)
            .senderNickname(senderNickname)
            .connectionType(connectionType)
            .messageContent(messageContent)
            .sentAt(sentAt)
            .readStatus(readStatus).build();
    }
}
