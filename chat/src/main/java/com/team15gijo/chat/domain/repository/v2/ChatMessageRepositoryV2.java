package com.team15gijo.chat.domain.repository.v2;

import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepositoryV2 extends
    MongoRepository<ChatMessageDocumentV2, String>,
    ChatMessageCustomRepositoryV2 {
}
