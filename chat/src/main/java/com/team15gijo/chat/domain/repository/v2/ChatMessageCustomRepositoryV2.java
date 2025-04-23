package com.team15gijo.chat.domain.repository.v2;

import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageCustomRepositoryV2 {

    Page<ChatMessageDocumentV2> searchMessages(
        UUID chatRoomId, LocalDateTime sentAt, String messageContent, Pageable pageable);
}
