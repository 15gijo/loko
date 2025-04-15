package com.team15gijo.notification.presentation.controller.v1;


import com.team15gijo.notification.application.dto.v1.message.ChatNotificationEventDto;
import com.team15gijo.notification.application.dto.v1.message.CommentNotificationEventDto;
import com.team15gijo.notification.application.dto.v1.message.FollowNotificationEventDto;
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

    @GetMapping("/comment")
    public void sendCommentKafkaMessage() {
        producer.sendCommentCreate(new CommentNotificationEventDto(1L, "정영", "잘 읽었습니다!"));
    }

    @GetMapping("/follow")
    public void sendFollowKafkaMessage() {
        producer.sendFollowCreate(new FollowNotificationEventDto(1L, "정영"));
    }

    @GetMapping("/chat")
    public void sendChatKafkaMessage() {
        producer.sendChatCreate(new ChatNotificationEventDto(1L, "정영", "오늘 사진 이쁘네요!"));
    }

}
