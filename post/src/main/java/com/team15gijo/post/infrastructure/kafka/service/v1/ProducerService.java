package com.team15gijo.post.infrastructure.kafka.service.v1;

import com.team15gijo.post.infrastructure.kafka.dto.v1.CommentCountEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.FeedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, FeedEventDto> postCreadtedkafkaTemplate;
    private final KafkaTemplate<String, CommentCountEventDto>    commentCountKafkaTemplate;


    public void sendMessage(String topic , String key, FeedEventDto event) {
        postCreadtedkafkaTemplate.send(topic, key, event);
    }

    /** 댓글 카운트 전용 event 발행 */
    public void sendCommentCountMessage(String topic, String key, CommentCountEventDto event) {
        commentCountKafkaTemplate.send(topic, key, event);
    }
}
