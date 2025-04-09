package com.team15gijo.notification.application.message;

import com.team15gijo.notification.application.dto.v1.CommentNotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaTestProducer {

    private final KafkaTemplate<String, CommentNotificationEvent> kafkaTemplate;

    public KafkaTestProducer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCommentCreate(CommentNotificationEvent event) {
        kafkaTemplate.send("COMMENT", event);
    }

}
