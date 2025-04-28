package com.team15gijo.search.infrastructure.kafka.service.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.search.application.service.v2.PostService;
import com.team15gijo.search.infrastructure.kafka.dto.v2.CommentCreatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.CommentDeletedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.EventType;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostCreatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostDeletedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostLikedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostUnlikedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostUpdatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostViewedEventDto;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsConsumerService {

    private final ObjectMapper objectMapper;
    private final PostService postService;

    @KafkaListener(groupId = "search-service", topics = "feed_events", containerFactory = "postKafkaListenerContainerFactory")
    public void consumeFromFeedEvents(String message) throws IOException {
        log.info("Kafka Consumer 메시지 수신: {}", message);

        JsonNode rootNode = objectMapper.readTree(message);
        EventType type = EventType.valueOf(rootNode.get("type").asText());

        switch (type) {
            case POST_CREATED -> {
                PostCreatedEventDto dto = objectMapper.treeToValue(rootNode, PostCreatedEventDto.class);
                postService.handlePostCreated(dto);
            }
            case POST_UPDATED -> {
                PostUpdatedEventDto dto = objectMapper.treeToValue(rootNode, PostUpdatedEventDto.class);
                postService.handlePostUpdated(dto);
            }
            case POST_DELETED -> {
                PostDeletedEventDto dto = objectMapper.treeToValue(rootNode, PostDeletedEventDto.class);
                postService.handlePostDeleted(dto);
            }
            case POST_VIEWED -> {
                PostViewedEventDto dto = objectMapper.treeToValue(rootNode, PostViewedEventDto.class);
                postService.handlePostViewed(dto);
            }
            case COMMENT_CREATED -> {
                CommentCreatedEventDto dto = objectMapper.treeToValue(rootNode, CommentCreatedEventDto.class);
                postService.handlePostCommented(dto);
            }
            case COMMENT_DELETED -> {
                CommentDeletedEventDto dto = objectMapper.treeToValue(rootNode, CommentDeletedEventDto.class);
                postService.handlePostCommentDeleted(dto);
            }
            case POST_LIKED -> {
                PostLikedEventDto dto = objectMapper.treeToValue(rootNode, PostLikedEventDto.class);
                postService.handlePostLiked(dto);
            }
            case POST_UNLIKED -> {
                PostUnlikedEventDto dto = objectMapper.treeToValue(rootNode, PostUnlikedEventDto.class);
                postService.handlePostUnliked(dto);
            }
            default -> log.warn("Unknown event type: {}", type);
        }
    }

}
