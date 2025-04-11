package com.team15gijo.feed.infrastructure.kafka.service.v1;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.feed.domain.model.Feed;
import com.team15gijo.feed.domain.repository.FeedRepository;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final ObjectMapper objectMapper;
    private final FeedRepository feedRepository;

    private static final String FEED_EVETNS_TOPIC = "feed_events";
    private static final String FEED_CONSUMER_GROUP_ID = "feed-service";

    @KafkaListener(groupId = FEED_CONSUMER_GROUP_ID, topics = FEED_EVETNS_TOPIC)
    public void consumeFromFeedEvents(String message) throws IOException {
        log.info("Kafka Consumer 메시지 수신: {}", message);

        JsonNode rootNode = objectMapper.readTree(message);
        EventType type = EventType.valueOf(rootNode.get("type").asText());

        switch (type) {
            case POST_CREATED -> {
                PostCreatedEventDto dto = objectMapper.treeToValue(rootNode, PostCreatedEventDto.class);
                handlePostCreated(dto);
            }
            case POST_UPDATED -> {
                PostUpdatedEventDto dto = objectMapper.treeToValue(rootNode, PostUpdatedEventDto.class);
                handlePostUpdated(dto);
            }
            case POST_DELETED -> {
                PostDeletedEventDto dto = objectMapper.treeToValue(rootNode, PostDeletedEventDto.class);
                handlePostDeleted(dto);
            }
            case POST_VIEWED -> {
                PostViewedEventDto dto = objectMapper.treeToValue(rootNode, PostViewedEventDto.class);
                handlePostViewed(dto);
            }
            case COMMENT_CREATED -> {
                CommentCreatedEventDto dto = objectMapper.treeToValue(rootNode, CommentCreatedEventDto.class);
                handlePostCommented(dto);
            }
            default -> log.warn("Unknown event type: {}", type);
        }

    }

    @Transactional
    public void handlePostCreated(PostCreatedEventDto dto) {
        log.info("✅ POST_CREATED received: {}", dto.toString());
        Feed feed = dto.toEntity();
        feedRepository.save(feed);
        log.info("Feed 정보 저장 완료 - postId: {}", feed.getPostId());
    }

    @Transactional
    public void handlePostUpdated(PostUpdatedEventDto dto) {
        log.info("✅ POST_UPDATED received: {}", dto.toString());
        // TODO: Redis 저장 / DB 업데이트 / 인덱싱 등 처리
        Feed feed = feedRepository.findById(dto.getPostId()).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", dto.getPostId());
            // 경우에 따라선 새로 insert할 수도 있음
            return;
        }
        feed.updateFeed(dto);
        feedRepository.save(feed);
        log.info("Feed 정보 수정 완료 - postId: {}", feed.getPostId());
    }

    @Transactional
    public void handlePostDeleted(PostDeletedEventDto dto) {
        log.info("🗑️ POST_DELETED received: {}", dto.toString());
        // TODO: DB 업데이트 / Redis 제거 / 상태 동기화
        Feed feed = feedRepository.findById(dto.getPostId()).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", dto.getPostId());
            return;
        }
        feedRepository.delete(feed);
    }

    @Transactional
    public void handlePostViewed(PostViewedEventDto dto) {
        log.info("👀 POST_VIEWED received: {}", dto.toString());
        // TODO: 조회수 누적 처리 등
        Feed feed = feedRepository.findById(dto.getPostId()).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", dto.getPostId());
            // 경우에 따라선 새로 insert할 수도 있음
            return;
        }
        feed.updateFeedViews(dto.getViews());
        feedRepository.save(feed);
        log.info("Feed 정보(조회수) 수정 완료 - postId: {}", feed.getPostId());
    }

    @Transactional
    public void handlePostCommented(CommentCreatedEventDto dto) {
        log.info("💬 POST_COMMENTED received: {}", dto.toString());
        // TODO: 댓글 수 증가 처리
    }


}