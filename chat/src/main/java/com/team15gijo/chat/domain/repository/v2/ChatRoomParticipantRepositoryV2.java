package com.team15gijo.chat.domain.repository.v2;

import com.team15gijo.chat.domain.model.v2.ChatRoomParticipantV2;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ChatRoomParticipantRepositoryV2 extends
    JpaRepository<ChatRoomParticipantV2, Long> {

    // 채팅방 참여자 배치 소프트 삭제
    @Transactional
    @Modifying
    @Query("UPDATE ChatRoomParticipantV2 p " +
        "SET p.activation = false," +
        "    p.deletedAt = :now, " +
        "    p.deletedBy = :userId, " +
        "    p.updatedAt = :now, " +
        "    p.updatedBy = :userId " +
        "WHERE p IN :participants")
    void softDeleteAllInBatch(
        @Param("participants") List<ChatRoomParticipantV2> participants,
        @Param("userId") Long userId,
        @Param("now") LocalDateTime now);
}
