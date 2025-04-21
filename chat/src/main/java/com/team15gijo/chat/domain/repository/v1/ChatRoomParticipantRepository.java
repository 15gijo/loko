package com.team15gijo.chat.domain.repository.v1;

import com.team15gijo.chat.domain.model.v1.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {
    void deleteByUserId(Long userId);
    void deleteByUserIdAndDeletedAtIsNull(Long userId);
}
