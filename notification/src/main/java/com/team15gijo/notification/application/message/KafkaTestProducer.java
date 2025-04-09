package com.team15gijo.notification.application.message;

import com.team15gijo.notification.application.dto.v1.message.ChatNotificationEventDto;
import com.team15gijo.notification.application.dto.v1.message.CommentNotificationEventDto;
import com.team15gijo.notification.application.dto.v1.message.FollowNotificationEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTestProducer {

    private final KafkaTemplate<String, CommentNotificationEventDto> commentKafkaTemplate;
    private final KafkaTemplate<String, FollowNotificationEventDto> followKafkaTemplate;
    private final KafkaTemplate<String, ChatNotificationEventDto> chatKafkaTemplate;



    public void sendCommentCreate(CommentNotificationEventDto event) {
        commentKafkaTemplate.send("COMMENT", event);
    }

    public void sendFollowCreate(FollowNotificationEventDto event) {
        followKafkaTemplate.send("FOLLOW", event);
    }

    public void sendChatCreate(ChatNotificationEventDto event) {
        chatKafkaTemplate.send("CHAT", event);
    }

}
