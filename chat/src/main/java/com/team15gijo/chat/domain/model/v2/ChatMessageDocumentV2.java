package com.team15gijo.chat.domain.model.v2;

import com.team15gijo.chat.application.dto.v2.ChatMessageResponseDtoV2;
import com.team15gijo.chat.presentation.dto.v2.ChatMessageRequestDtoV2;
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
public class ChatMessageDocumentV2 {
    @Id
    private String _id;
    private UUID chatRoomId;
    @CreatedBy
    @Column(name = "sender_by", nullable = false, updatable = false)
    private Long senderId;
    private Long receiverId;
    private String receiverNickname;
    private ConnectionTypeV2 connectionType;
    private String messageContent;
    private ChatMessageTypeV2 chatMessageType;
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

    public ChatMessageResponseDtoV2 toResponse() {
        return ChatMessageResponseDtoV2.builder()
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

    // 채팅 입장 메시지 전송
    public static ChatMessageDocumentV2 createEnterMessageDocument(
        UUID chatRoomId, Long senderId, Long receiverId, String receiverNickname, String messageContent) {
        return ChatMessageDocumentV2.builder()
            .chatRoomId(chatRoomId)
            .senderId(senderId)
            .receiverId(receiverId)
            .receiverNickname(receiverNickname)
            .connectionType(ConnectionTypeV2.ENTER)
            .chatMessageType(ChatMessageTypeV2.TEXT)
            .messageContent(messageContent)
            .sentAt(LocalDateTime.now())
            .build();
    }

    // 채팅 메시지 전송
    public static ChatMessageDocumentV2 createChatMessageDocument(
        ChatMessageRequestDtoV2 request) {
        return ChatMessageDocumentV2.builder()
            .chatRoomId(request.getChatRoomId())
            .senderId(request.getSenderId())
            .receiverId(request.getReceiverId())
            .receiverNickname(request.getReceiverNickname())
            .connectionType(ConnectionTypeV2.CHAT)
            .chatMessageType(ChatMessageTypeV2.TEXT)
            .messageContent(request.getMessageContent())
            .sentAt(LocalDateTime.now())
            .build();
    }

    // 채팅 에러 메시지 전송
    public static ChatMessageDocumentV2 createErrorMessageDocument(
        UUID chatRoomId, Long senderId, String messageContent) {
        return ChatMessageDocumentV2.builder()
            .chatRoomId(chatRoomId)
            .senderId(senderId)
            .connectionType(ConnectionTypeV2.ENTER)
            .chatMessageType(ChatMessageTypeV2.TEXT)
            .messageContent(messageContent)
            .sentAt(LocalDateTime.now())
            .build();
    }
}
