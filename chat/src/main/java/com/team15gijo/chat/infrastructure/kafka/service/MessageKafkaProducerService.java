package com.team15gijo.chat.infrastructure.kafka.service;

import com.team15gijo.chat.infrastructure.kafka.dto.ChatMessageEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageKafkaProducerService {

    private final KafkaTemplate<String, ChatMessageEventDto> chatMessageKafkaTemplate;

    public void sendChatMessage(String topic, ChatMessageEventDto chatMessageEventDto) {
        log.info("[MessageKafkaProducerService] sendChatMessage 시작 topic: {}", topic);
        chatMessageKafkaTemplate.send(topic, chatMessageEventDto);
    }
}
