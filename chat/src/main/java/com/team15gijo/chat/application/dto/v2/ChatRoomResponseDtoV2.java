package com.team15gijo.chat.application.dto.v2;

import com.team15gijo.chat.domain.model.v2.ChatRoomParticipantV2;
import com.team15gijo.chat.domain.model.v2.ChatRoomTypeV2;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDtoV2 {
    private UUID chatRoomId;
    private ChatRoomTypeV2 chatRoomType;
    private Set<ChatRoomParticipantV2> chatRoomParticipants;
}
