package com.team15gijo.comment.infrastructure.kafka.service;

import com.team15gijo.comment.infrastructure.kafka.dto.CommentNotificationEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, CommentNotificationEventDto> kafkaTemplate;

    public void sendCommentCreate(CommentNotificationEventDto event) {
        kafkaTemplate.send("COMMENT", event);
    }

}
