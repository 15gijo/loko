package com.team15gijo.notification.presentation.controller.v1;


import com.team15gijo.notification.application.dto.v1.CommentNotificationEvent;
import com.team15gijo.notification.application.message.KafkaTestProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka/test")
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaTestProducer producer;

    @GetMapping
    public void sendKafkaMessage() {
        producer.sendCommentCreate(new CommentNotificationEvent(1L, "정영", "잘 읽었습니다!"));
    }

}
