package com.team15gijo.chat.domain.repository.v1;

import com.team15gijo.chat.domain.model.v1.ChatMessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageDocument, String> {
}
