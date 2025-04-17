package com.team15gijo.post.infrastructure.kafka.util;

import com.team15gijo.post.domain.model.v2.PostV2;
import com.team15gijo.post.infrastructure.kafka.dto.v1.CommentCreatedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.CommentDeletedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.EventType;
import com.team15gijo.post.infrastructure.kafka.dto.v1.FeedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.PostCreatedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.PostDeletedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.PostLikedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.PostUnlikedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.PostUpdatedEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.PostViewedEventDto;
import com.team15gijo.post.infrastructure.kafka.service.v1.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaUtil {

    private final ProducerService producerService;

    /**
     * kafka 이벤트 발행
     */
    public void sendKafkaEvent(EventType eventType, PostV2 post, String topic) {
        if (topic == null || post == null || eventType == null) return;
        FeedEventDto feedEventDto = createEventMessageByType(eventType, post);
        producerService.sendMessage(topic, null, feedEventDto);
    }

    public FeedEventDto createEventMessageByType(EventType eventType, PostV2 post) {
        if (eventType == null || post == null) return null;

        return switch (eventType) {
            case POST_CREATED -> PostCreatedEventDto.from(post);
            case POST_UPDATED -> PostUpdatedEventDto.from(post);
            case POST_DELETED -> PostDeletedEventDto.from(post);
            case POST_VIEWED -> PostViewedEventDto.from(post);
            case COMMENT_CREATED -> CommentCreatedEventDto.from(post);
            case COMMENT_DELETED -> CommentDeletedEventDto.from(post);
            case POST_LIKED -> PostLikedEventDto.from(post);
            case POST_UNLIKED -> PostUnlikedEventDto.from(post);
            default -> null;
        };
    }

}
