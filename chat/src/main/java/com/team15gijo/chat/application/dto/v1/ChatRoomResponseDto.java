package com.team15gijo.chat.application.dto.v1;

import com.team15gijo.chat.domain.model.v1.ChatRoomParticipant;
import com.team15gijo.chat.domain.model.v1.ChatRoomType;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDto {
    private UUID chatRoomId;
    private ChatRoomType chatRoomType;
    private Set<ChatRoomParticipant> chatRoomParticipants;
}
