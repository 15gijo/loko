package com.team15gijo.chat.domain.repository;

import com.team15gijo.chat.domain.model.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {
}
