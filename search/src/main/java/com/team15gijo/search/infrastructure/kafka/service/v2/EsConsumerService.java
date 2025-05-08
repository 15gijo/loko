package com.team15gijo.search.infrastructure.kafka.service.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.common.exception.CustomException;
import com.team15gijo.search.application.service.v2.PostService;
import com.team15gijo.search.domain.exception.SearchDomainExceptionCode;
import com.team15gijo.search.domain.model.PostUpdateDlq;
import com.team15gijo.search.domain.repository.PostUpdateDlqRepository;
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
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsConsumerService {

    private final ObjectMapper objectMapper;
    private final PostService postService;
    private final PostUpdateDlqRepository dlqRepository;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, maxDelay = 3000, random = true), // 1~3초 랜덤 재시도
            dltStrategy = DltStrategy.FAIL_ON_ERROR, // 재시도 실패 후 DLQ 이동
            dltTopicSuffix = "-search-dlt", // DLQ 토픽 접미사
            exclude = { // 즉시 DLQ 전송 예외 클래스 리스트
                    IllegalArgumentException.class, // 데이터 파싱 오류 같은 경우는 바로 DLQ
                    NullPointerException.class      // null 포인터 발생 시도 바로 DLQ
            }
    )
    @KafkaListener(groupId = "search-service", topics = "feed_events", containerFactory = "postKafkaListenerContainerFactory")
    public void consumeFromFeedEvents(String message) throws IOException {
        log.info("Kafka Consumer 메시지 수신: {}", message);

        try {
            JsonNode rootNode = objectMapper.readTree(message);
            EventType type = EventType.valueOf(rootNode.get("type").asText());

            // ✨ (추가) 테스트용 예외 강제 발생
            if (message.contains("retry-test")) {  // 메시지 안에 특정 키워드가 있으면 강제 오류
                log.error("💥 테스트용 예외 발생: 강제 RuntimeException 던짐!");
                throw new RuntimeException("테스트용 RuntimeException 발생");
            }

            switch (type) {
                case POST_CREATED -> {
                    PostCreatedEventDto dto = objectMapper.treeToValue(rootNode,
                            PostCreatedEventDto.class);
                    postService.handlePostCreated(dto);
                }
                case POST_UPDATED -> {
                    PostUpdatedEventDto dto = objectMapper.treeToValue(rootNode,
                            PostUpdatedEventDto.class);
                    postService.handlePostUpdated(dto);
                }
                case POST_DELETED -> {
                    PostDeletedEventDto dto = objectMapper.treeToValue(rootNode,
                            PostDeletedEventDto.class);
                    postService.handlePostDeleted(dto);
                }
                case POST_VIEWED -> {
                    PostViewedEventDto dto = objectMapper.treeToValue(rootNode,
                            PostViewedEventDto.class);
                    postService.handlePostViewed(dto);
                }
                case COMMENT_CREATED -> {
                    CommentCreatedEventDto dto = objectMapper.treeToValue(rootNode,
                            CommentCreatedEventDto.class);
                    postService.handlePostCommented(dto);
                }
                case COMMENT_DELETED -> {
                    CommentDeletedEventDto dto = objectMapper.treeToValue(rootNode,
                            CommentDeletedEventDto.class);
                    postService.handlePostCommentDeleted(dto);
                }
                case POST_LIKED -> {
                    PostLikedEventDto dto = objectMapper.treeToValue(rootNode,
                            PostLikedEventDto.class);
                    postService.handlePostLiked(dto);
                }
                case POST_UNLIKED -> {
                    PostUnlikedEventDto dto = objectMapper.treeToValue(rootNode,
                            PostUnlikedEventDto.class);
                    postService.handlePostUnliked(dto);
                }
                default -> log.warn("Unknown event type: {}", type);
            }
        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 중 예외 발생", e);
            throw e; // 반드시 다시 던져야 재시도 + DLQ 작동함
        }
    }

    // 2. DLQ 처리용 Kafka Listener
    @KafkaListener(topics = "feed_events-search-dlt", groupId = "search-service", containerFactory = "postKafkaListenerContainerFactory")
    public void handleEventDlt(String message) throws IOException {
        log.error("🔥 DLQ로 이동된 메시지 수신: {}", message);
        // 여기서 Slack 알림 보내거나 Kibana/DB 저장 추가
        JsonNode rootNode = objectMapper.readTree(message);
        EventType type = EventType.valueOf(rootNode.get("type").asText());

        try {
            PostUpdateDlq dlq = PostUpdateDlq.builder()
                    .type(String.valueOf(type))
                    .payload(message)
                    .errorMessage("Post 업데이트 DLT 수신")
                    .resolved(false)
                    .build();

            dlqRepository.save(dlq);
        } catch (Exception e) {
            log.error("DLQ 저장 실패", e);
            throw new CustomException(SearchDomainExceptionCode.DLT_SAVE_FAIL);
        }
    }
}
