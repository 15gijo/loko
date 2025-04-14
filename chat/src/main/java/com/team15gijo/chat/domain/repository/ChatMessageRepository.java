package com.team15gijo.chat.domain.repository;

import com.team15gijo.chat.domain.model.ChatMessageDocument;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatMessageRepository extends MongoRepository<ChatMessageDocument, String> {

    // 삭제되지 않은 채팅방의 메시지 조회
    @Query("{ 'deletedAt' : null }")
    List<ChatMessageDocument> findByChatRoomIdAndSenderId(UUID chatRoomId, Long senderId);

    @Query("{ 'deletedAt' : null }")
    List<ChatMessageDocument> findByChatRoomId(UUID chatRoomId);
}
