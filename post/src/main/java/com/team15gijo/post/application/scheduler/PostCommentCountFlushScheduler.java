package com.team15gijo.post.application.scheduler;

import com.team15gijo.post.domain.repository.v2.PostRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCommentCountFlushScheduler {

    private final PostRepositoryV2 postRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_COMMENT_COUNT_HASH = "comments:buffer";
    private static final String REDIS_COMMENT_DIRTY_SET = "comments:dirty-keys";

    /** 30초마다 Redis 버퍼에 쌓인 댓글 수를 DB에 반영 */
    @Scheduled(fixedRate = 3000)
    @Transactional
    public void flushCommentCountsToDb() {
        Set<Object> dirtyPostIds = redisTemplate.opsForSet().members(REDIS_COMMENT_DIRTY_SET);
        if (dirtyPostIds == null || dirtyPostIds.isEmpty()) {
            return;
        }

        for (Object postIdObj : dirtyPostIds) {
            String postIdStr = postIdObj.toString();
            UUID postId;
            try {
                postId = UUID.fromString(postIdStr);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid postId in dirty set: {}", postIdStr);
                // 잘못된 키 정리
                redisTemplate.opsForSet().remove(REDIS_COMMENT_DIRTY_SET, postIdStr);
                redisTemplate.opsForHash().delete(REDIS_COMMENT_COUNT_HASH, postIdStr);
                continue;
            }

            Object rawDelta = redisTemplate.opsForHash().get(REDIS_COMMENT_COUNT_HASH, postIdStr);
            int delta;
            try {
                delta = Integer.parseInt(rawDelta.toString());
            } catch (Exception e) {
                log.warn("Invalid delta value for postId {}: {}", postIdStr, rawDelta);
                // 스킵만 하고 다음
                continue;
            }

            // DB 업데이트: comment_count = comment_count + delta
            int updated = postRepository.incrementCommentCount(postId, delta);
            if (updated > 0) {
                log.info("Flushed comment count +{} to DB for postId {}", delta, postIdStr);
                // Redis 버퍼에서 제거
                redisTemplate.opsForSet().remove(REDIS_COMMENT_DIRTY_SET, postIdStr);
                redisTemplate.opsForHash().delete(REDIS_COMMENT_COUNT_HASH, postIdStr);
            } else {
                log.warn("Post not found or not updated for postId {}", postIdStr);
            }
        }
    }
}
