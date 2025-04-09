package com.team15gijo.chat.presentation.dto.v1;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ChatMessageRequestDto {
    private UUID chatRoomId;
    private Long senderId;
    private String message;
}
