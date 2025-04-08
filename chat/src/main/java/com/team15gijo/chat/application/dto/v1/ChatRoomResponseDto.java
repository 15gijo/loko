package com.team15gijo.chat.application.dto.v1;

import com.team15gijo.chat.domain.model.ChatRoomType;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomResponseDto {
    private UUID id;
    private ChatRoomType chatRoomType;
}
