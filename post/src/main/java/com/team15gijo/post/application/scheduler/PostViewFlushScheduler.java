package com.team15gijo.post.application.scheduler;

import com.team15gijo.post.domain.repository.v2.PostRepositoryV2;
import com.team15gijo.post.infrastructure.kafka.dto.v1.EventType;
import com.team15gijo.post.infrastructure.kafka.util.KafkaUtil;
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
public class PostViewFlushScheduler {

    private final PostRepositoryV2 postRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaUtil kafkaUtil;

    private static final String REDIS_VIEW_COUNT_HASH_KEY = "views:buffer";
    private static final String REDIS_VIEW_DIRTY_KEY_SET = "views:dirty-keys";
    private static final String FEED_EVENTS_TOPIC = "feed_events";

    @Scheduled(fixedRate = 30000) // 매 30초마다 실행
    @Transactional
    public void flushViewCountsToDb() {
        Set<Object> dirtyPostIds = redisTemplate.opsForSet().members(REDIS_VIEW_DIRTY_KEY_SET);
        if (dirtyPostIds == null || dirtyPostIds.isEmpty()) {
            return;
        }

        for (Object postIdObj : dirtyPostIds) {
            String postIdStr = postIdObj.toString();
            UUID postId;

            try {
                postId = UUID.fromString(postIdStr);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid postId format in dirty set: {}", postIdStr);
                redisTemplate.opsForSet().remove(REDIS_VIEW_DIRTY_KEY_SET, postIdStr);
                redisTemplate.opsForHash().delete(REDIS_VIEW_COUNT_HASH_KEY, postIdStr);
                continue;
            }

            Object rawViewCount = redisTemplate.opsForHash().get(REDIS_VIEW_COUNT_HASH_KEY, postIdStr);
            int viewCount;
            try {
                viewCount = Integer.parseInt(rawViewCount.toString());
            } catch (Exception e) {
                log.warn("Invalid view count for postId {}: {}", postIdStr, rawViewCount);
                continue; // 잘못된 값은 스킵
            }

            postRepository.findById(postId).ifPresent(post -> {
                post.increaseViews(viewCount); // views += count
                postRepository.save(post);

                kafkaUtil.sendKafkaEvent(EventType.POST_VIEWED, post, FEED_EVENTS_TOPIC);

                // 처리 완료 후 Redis에서 제거
                redisTemplate.opsForSet().remove(REDIS_VIEW_DIRTY_KEY_SET, postIdStr);
                redisTemplate.opsForHash().delete(REDIS_VIEW_COUNT_HASH_KEY, postIdStr);
            });
        }
    }
}
