package com.team15gijo.chat.domain.repository;

import com.team15gijo.chat.domain.model.ChatRoom;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
}
