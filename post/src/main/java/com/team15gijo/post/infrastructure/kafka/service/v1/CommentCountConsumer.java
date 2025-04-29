
package com.team15gijo.post.infrastructure.kafka.service.v1;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.team15gijo.post.domain.exception.PostDomainException;
import com.team15gijo.post.domain.exception.PostDomainExceptionCode;
import com.team15gijo.post.domain.model.v2.PostV2;
import com.team15gijo.post.domain.repository.v2.PostRepositoryV2;
import com.team15gijo.post.infrastructure.kafka.dto.v1.EventType;
import com.team15gijo.post.infrastructure.kafka.util.KafkaUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCountConsumer {

    private final ObjectMapper objectMapper;
    private final PostRepositoryV2 postRepo;
    private final RedisTemplate<String,Object> redisTemplate;
    private final KafkaUtil kafkaUtil;

    private static final Logger log = LoggerFactory.getLogger(CommentCountConsumer.class);
    private static final String FEED_EVENTS_TOPIC = "feed_events";

    @KafkaListener(topics   = "COMMENT_COUNT_EVENTS", groupId  = "post-comment-count-flusher")
    @Transactional
    public void handle(String message) throws JsonProcessingException {
        JsonNode root   = objectMapper.readTree(message);
        UUID     postId = UUID.fromString(root.get("postId").asText());
        int      delta  = root.get("delta").asInt();

        // 1) 댓글 카운트 경량 업데이트
        int updatedRows = postRepo.incrementCommentCount(postId, delta);
        if (updatedRows == 0) {
            log.warn("존재하지 않는 게시글(postId={})에 대한 댓글 카운트 이벤트를 스킵합니다.", postId);
            return;
        }

        // 2) 피드용 Post 조회 및 이벤트 발행
        postRepo.findById(postId).ifPresent(post -> {
            kafkaUtil.sendKafkaEvent(EventType.COMMENT_CREATED, post, FEED_EVENTS_TOPIC);

            // 3) Redis 버퍼/dirty-key 제거
            String key = postId.toString();
            redisTemplate.opsForHash().delete("comments:buffer", key);
            redisTemplate.opsForSet().remove("comments:dirty-keys", key);
        });
    }

}
