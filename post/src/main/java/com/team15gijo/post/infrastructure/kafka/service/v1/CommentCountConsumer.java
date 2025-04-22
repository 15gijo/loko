
package com.team15gijo.post.infrastructure.kafka.service.v1;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.team15gijo.post.domain.repository.v2.PostRepositoryV2;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @KafkaListener(topics   = "COMMENT_COUNT_EVENTS", groupId  = "post-comment-count-flusher")
    @Transactional
    public void handle(String message) throws JsonProcessingException {
        // 메시지를 JsonNode로 읽어들임
        JsonNode root = objectMapper.readTree(message);

        // 필요한 필드(postId, delta) 추출
        UUID postId = UUID.fromString(root.get("postId").asText());
        int delta  = root.get("delta").asInt();

        // 경량 쿼리로 DB 업데이트
        postRepo.incrementCommentCount(postId, delta);

        //  Redis buffer 클리어
        String key = postId.toString();
        redisTemplate.opsForHash().delete("comments:buffer", key);
        redisTemplate.opsForSet().remove("comments:dirty-keys", key);
    }
}
