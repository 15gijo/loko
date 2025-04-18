package com.team15gijo.chat.presentation.dto.v2;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomParticipantResponseDtoV2 {
    private UUID id;
    private Long userId;
    private Boolean activation;
}
