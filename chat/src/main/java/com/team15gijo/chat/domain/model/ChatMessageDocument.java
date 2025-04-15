package com.team15gijo.chat.domain.model;

import com.team15gijo.chat.presentation.dto.v1.ChatMessageResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "chat")
public class ChatMessageDocument {
    @Id
    private String _id;
    private UUID chatRoomId;
    @CreatedBy
    @Column(name = "sender_by", nullable = false, updatable = false)
    private Long senderId;
    private Long receiverId;
    private String receiverNickname;
    private ConnectionType connectionType;
    private String messageContent;
    private ChatMessageType chatMessageType;
    @CreatedDate
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;
    private Boolean readStatus;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "deleted_by")
    private Long deletedBy;

    // 삭제 처리 메서드
    public void softDelete(Long deletedBy) {
        if (deletedAt == null) {
            deletedAt = LocalDateTime.now();
            this.deletedBy = deletedBy;
        }
    }

    public ChatMessageResponseDto toResponse() {
        return ChatMessageResponseDto.builder()
            .id(_id)
            .senderId(senderId)
            .receiverId(receiverId)
            .receiverNickname(receiverNickname)
            .connectionType(connectionType)
            .messageContent(messageContent)
            .sentAt(sentAt)
            .readStatus(readStatus)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .build();
    }
}
