package com.team15gijo.post.infrastructure.kafka.util;

import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.infrastructure.kafka.dto.v1.EventType;
import com.team15gijo.post.infrastructure.kafka.dto.v1.FeedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.PostCreatedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.PostDeletedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.PostUpdatedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.PostViewedEventDto;
import com.team15gijo.post.infrastructure.kafka.service.v1.ProducerTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaUtil {

    private final ProducerTestService producerService;

    /**
     * kafka 이벤트 발행
     */
    public void sendKafkaEvent(EventType eventType, Post post, String topic) {
        if (topic == null || post == null || eventType == null) return;
        FeedEventDto feedEventDto = createEventMessageByType(eventType, post);
        producerService.sendMessage(topic, null, feedEventDto);
    }

    public FeedEventDto createEventMessageByType(EventType eventType, Post post) {
        if (eventType == null || post == null) return null;

        return switch (eventType) {
            case POST_CREATED -> PostCreatedEventDto.from(post);
            case POST_UPDATED -> PostUpdatedEventDto.from(post);
            case POST_DELETED -> PostDeletedEventDto.from(post);
            case POST_VIEWED -> PostViewedEventDto.from(post);
            default -> null;
        };
    }

}
