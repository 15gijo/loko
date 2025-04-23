package com.team15gijo.chat.domain.repository.v2;

import com.team15gijo.chat.domain.model.v2.ChatRoomV2;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ChatRoomRepositoryV2 extends JpaRepository<ChatRoomV2, UUID> {
    Optional<ChatRoomV2> findByChatRoomId(UUID chatRoomId);

    // 채팅방 참여자 테이블과 조인하여 userId가 포함되는 채팅방만 조회
    @Transactional(readOnly = true)
    @Query("SELECT DISTINCT cr "
        + "FROM ChatRoomV2 cr "
        + "JOIN FETCH cr.chatRoomParticipant cp "
        + "WHERE cp.userId = :userId ")
    Page<ChatRoomV2> findChatRoomByParticipantUserId(
        @Param("userId") Long userId, Pageable pageable);

    // 채팅방 비활성화 시 chatRoomId에 대한 참여자 정보(userId) 조회
    @Transactional(readOnly = true)
    @Query("SELECT cr "
        + "FROM ChatRoomV2 cr "
        + "JOIN FETCH cr.chatRoomParticipant "
        + "WHERE cr.chatRoomId = :chatRoomId")
    Optional<ChatRoomV2> findChatRoomWithParticipantsByChatRoomId(
        @Param("chatRoomId") UUID chatRoomId);
}
