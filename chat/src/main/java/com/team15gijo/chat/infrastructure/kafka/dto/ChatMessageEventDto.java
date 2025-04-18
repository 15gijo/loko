package com.team15gijo.chat.infrastructure.kafka.dto;

import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import com.team15gijo.chat.domain.model.v2.ChatMessageTypeV2;
import com.team15gijo.chat.domain.model.v2.ConnectionTypeV2;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageEventDto {
    private UUID chatRoomId; // 채팅방 id
    private Long senderId; // 보내는 사람 id
    private Long receiverId; // 받는 사람 id
    private String receiverNickname; // 받는 사람 닉네임
    private ConnectionTypeV2 connectionType; // 메시지 연결 종류(ENTER, CHAT, EXIT)
    private ChatMessageTypeV2 chatMessageType; // 메시지 전송 종류(TEXT, IMAGE, VIDEO)
    private String messageContent; // 메시지
    private LocalDateTime sentAt; // 보낸 시간

    public static ChatMessageEventDto from(ChatMessageDocumentV2 chatMessage) {
        return ChatMessageEventDto.builder()
            .chatRoomId(chatMessage.getChatRoomId())
            .senderId(chatMessage.getSenderId())
            .receiverId(chatMessage.getReceiverId())
            .receiverNickname(chatMessage.getReceiverNickname())
            .connectionType(chatMessage.getConnectionType())
            .chatMessageType(chatMessage.getChatMessageType())
            .messageContent(chatMessage.getMessageContent())
            .sentAt(LocalDateTime.now())
            .build();
    }

    public static ChatMessageDocumentV2 from(ChatMessageEventDto chatMessageEventDto) {
        return ChatMessageDocumentV2.builder()
            .chatRoomId(chatMessageEventDto.getChatRoomId())
            .senderId(chatMessageEventDto.getSenderId())
            .receiverId(chatMessageEventDto.getReceiverId())
            .receiverNickname(chatMessageEventDto.getReceiverNickname())
            .connectionType(chatMessageEventDto.getConnectionType())
            .chatMessageType(chatMessageEventDto.getChatMessageType())
            .messageContent(chatMessageEventDto.getMessageContent())
            .sentAt(chatMessageEventDto.getSentAt())
            .build();
    }
}
