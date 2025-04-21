package com.team15gijo.chat.domain.repository.v2;

import com.team15gijo.chat.domain.model.v2.ChatRoomV2;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepositoryV2 extends JpaRepository<ChatRoomV2, UUID> {
    Optional<ChatRoomV2> findByChatRoomIdAndDeletedAtNull(UUID chatRoomId);
}
