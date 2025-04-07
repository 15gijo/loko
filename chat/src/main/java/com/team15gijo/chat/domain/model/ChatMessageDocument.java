package com.team15gijo.chat.domain.model;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat")
public class ChatMessageDocument {
    @Id
    private String _id;
    private UUID chatRoomId;
    private UUID senderId;
    private ConnectionType connectionType;
    private String messageContent;
    private MessageType messageType;
    private LocalDateTime sentAt;
    private Boolean readStatus;
}
