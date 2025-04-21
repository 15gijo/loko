package com.team15gijo.chat.application.dto.v2;

import com.team15gijo.chat.domain.model.v2.ConnectionTypeV2;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDtoV2 {
    private String id;
    private Long senderId;
    private Long receiverId;
    private String receiverNickname;
    private ConnectionTypeV2 connectionType;
    private String messageContent;
    private LocalDateTime sentAt;
    private Boolean readStatus;
    private LocalDateTime deletedAt;
    private Long deletedBy;
}
