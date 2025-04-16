package com.team15gijo.chat.infrastructure.kafka.service;

import com.team15gijo.chat.infrastructure.kafka.dto.ChatNotificationEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationKafkaProducerService {

    private final KafkaTemplate<String, ChatNotificationEventDto> kafkaTemplate;

    public void sendChatCreate(ChatNotificationEventDto event) {
        kafkaTemplate.send("CHAT", event);
    }

}
