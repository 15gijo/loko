package com.team15gijo.chat.domain.repository;

import com.team15gijo.chat.domain.model.ChatRoom;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findByChatRoomId(UUID chatRoomId);
    Optional<ChatRoom> findByChatRoomIdAndDeletedAtNull(UUID chatRoomId);
}
