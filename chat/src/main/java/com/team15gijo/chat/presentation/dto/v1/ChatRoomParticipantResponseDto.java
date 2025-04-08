package com.team15gijo.chat.presentation.dto.v1;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomParticipantResponseDto {
    private UUID chatRoomId;
    private Long userId;
    private Boolean activation;
}
