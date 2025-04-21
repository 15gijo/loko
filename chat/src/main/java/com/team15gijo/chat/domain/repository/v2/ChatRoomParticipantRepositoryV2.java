package com.team15gijo.chat.domain.repository.v2;

import com.team15gijo.chat.domain.model.v2.ChatRoomParticipantV2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomParticipantRepositoryV2 extends
    JpaRepository<ChatRoomParticipantV2, Long> {

    void deleteByUserId(Long userId);
}
