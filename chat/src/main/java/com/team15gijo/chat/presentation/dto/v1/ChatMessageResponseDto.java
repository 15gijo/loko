package com.team15gijo.chat.presentation.dto.v1;

import java.time.LocalDateTime;
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
    private Long senderId;
    private String senderNickname;
    private String message;
    private LocalDateTime sentAt;
    private Boolean readStatus;
}
