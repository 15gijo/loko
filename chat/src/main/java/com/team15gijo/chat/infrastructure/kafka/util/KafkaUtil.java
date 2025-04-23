package com.team15gijo.chat.infrastructure.kafka.util;

import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import com.team15gijo.chat.infrastructure.kafka.dto.ChatMessageEventDto;
import com.team15gijo.chat.infrastructure.kafka.service.MessageKafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaUtil {
    private final MessageKafkaProducerService messageKafkaProducerService;

    /**
     * kafka 이벤트 발행
     */
    public void sendKafkaEvent(String topic, ChatMessageDocumentV2 chatMessageDocument) {
        log.info("[KafkaUtil] sendKafkaEvent 시작 topic: {}", topic);
        ChatMessageEventDto eventDto = ChatMessageEventDto.from(chatMessageDocument);
        messageKafkaProducerService.sendChatMessage(topic, eventDto);
    }

}
