package com.team15gijo.chat.domain.repository;

import com.team15gijo.chat.domain.model.ChatMessageDocument;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageDocument, String> {

    List<ChatMessageDocument> findByChatRoomIdAndSenderId(UUID chatRoomId, Long senderId);
}
