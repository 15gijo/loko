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
public class ChatRoomParticipantRequestDtoV2 {
    private UUID chatRoomId;
}
