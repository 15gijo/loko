package com.team15gijo.feed.infrastructure.kafka.service.v1;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.feed.application.service.v1.FeedEventService;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final ObjectMapper objectMapper;
    private final FeedEventService feedEventService;

    private static final String FEED_EVENTS_TOPIC = "feed_events";
    private static final String FEED_CONSUMER_GROUP_ID = "feed-service";

    @KafkaListener(groupId = FEED_CONSUMER_GROUP_ID, topics = FEED_EVENTS_TOPIC)
    public void consumeFromFeedEvents(String message) throws IOException {
        log.info("Kafka Consumer 메시지 수신: {}", message);

        JsonNode rootNode = objectMapper.readTree(message);
        EventType type = EventType.valueOf(rootNode.get("type").asText());

        switch (type) {
            case POST_CREATED -> {
                PostCreatedEventDto dto = objectMapper.treeToValue(rootNode, PostCreatedEventDto.class);
                feedEventService.handlePostCreated(dto);
            }
            case POST_UPDATED -> {
                PostUpdatedEventDto dto = objectMapper.treeToValue(rootNode, PostUpdatedEventDto.class);
                feedEventService.handlePostUpdated(dto);
            }
            case POST_DELETED -> {
                PostDeletedEventDto dto = objectMapper.treeToValue(rootNode, PostDeletedEventDto.class);
                feedEventService.handlePostDeleted(dto);
            }
            case POST_VIEWED -> {
                PostViewedEventDto dto = objectMapper.treeToValue(rootNode, PostViewedEventDto.class);
                feedEventService.handlePostViewed(dto);
            }
            case COMMENT_CREATED -> {
                CommentCreatedEventDto dto = objectMapper.treeToValue(rootNode, CommentCreatedEventDto.class);
                feedEventService.handlePostCommented(dto);
            }
            default -> log.warn("Unknown event type: {}", type);
        }
    }

}