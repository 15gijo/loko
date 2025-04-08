package com.team15gijo.chat.domain.repository;

import com.team15gijo.chat.domain.model.ChatRoom;
import com.team15gijo.chat.domain.model.ChatRoomParticipant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    List<ChatRoomParticipant> findByUserId(Long userId);

    Optional<ChatRoomParticipant> findByUserIdAndChatRoom_ChatRoomId(Long userId, UUID chatRoomId);

    default List<ChatRoom> findChatRoomsByUserId(Long userId) {
        List<ChatRoomParticipant> participants = findByUserId(userId);
        return participants.stream()
            .map(ChatRoomParticipant::getChatRoom)
            .toList();
    }
}
