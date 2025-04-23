package com.team15gijo.chat.presentation.dto.v2;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ChatMessageRequestDtoV2 {
    private UUID chatRoomId;
    private Long senderId;
    private String senderNickname;
    private Long receiverId;
    private String receiverNickname;
    private String messageContent;
}
