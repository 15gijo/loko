package com.team15gijo.chat.domain.repository;

import com.team15gijo.chat.domain.model.ChatMessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageDocument, String> {

}
