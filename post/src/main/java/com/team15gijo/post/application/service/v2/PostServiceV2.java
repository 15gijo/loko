package com.team15gijo.post.application.service.v2;

import com.team15gijo.post.domain.model.v2.HashtagV2;
import com.team15gijo.post.infrastructure.client.ai.AiClient;
import com.team15gijo.post.infrastructure.client.ai.HashtagRequestDto;
import com.team15gijo.post.infrastructure.client.ai.HashtagResponseDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.CommentCountEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v2.PostElasticsearchRequestDto;
import com.team15gijo.post.infrastructure.kafka.service.v2.ElasticsearchKafkaProducerService;
import com.team15gijo.post.infrastructure.kafka.util.KafkaUtil;
import com.team15gijo.post.domain.exception.PostDomainException;
import com.team15gijo.post.domain.exception.PostDomainExceptionCode;
import com.team15gijo.post.domain.model.v2.PostV2;
import com.team15gijo.post.domain.repository.v2.HashtagRepositoryV2;
import com.team15gijo.post.domain.repository.v2.PostRepositoryV2;
import com.team15gijo.post.infrastructure.kafka.dto.v1.EventType;
import com.team15gijo.post.presentation.dto.v2.PostRequestDtoV2;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceV2 {

    private final PostRepositoryV2 postRepository;
    private final HashtagRepositoryV2 hashtagRepository;
    private final KafkaUtil kafkaUtil;
    private final AiClient aiClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ElasticsearchKafkaProducerService elasticsearchKafkaProducerService;

    private static final String FEED_EVENTS_TOPIC = "feed_events";
    private static final String REDIS_VIEW_COUNT_HASH_KEY = "views:buffer";
    private static final String REDIS_VIEW_DIRTY_KEY_SET = "views:dirty-keys";

    private static final String REDIS_COMMENT_COUNT_HASH   = "comments:buffer";
    private static final String REDIS_COMMENT_DIRTY_SET    = "comments:dirty-keys";


    /**
     * 게시글 생성
     */
    public PostV2 createPost(long userId, String username, String region, PostRequestDtoV2 request) {
        PostV2 post = PostV2.createPost(userId, username, region, request.getPostContent());
        post = postRepository.save(post);


        // 3) AI 마이크로서비스 호출
        HashtagResponseDto aiResp = aiClient.recommendHashtags(
                new HashtagRequestDto(request.getPostContent())
        );

        // 4) 추천된 해시태그를 엔티티로 변환하여 Post 에 매핑
        for (String tagName : aiResp.getHashtags()) {
            HashtagV2 tag = hashtagRepository
                    .findByHashtagName(tagName)
                    .orElseGet(() -> hashtagRepository.save(
                            HashtagV2.builder()
                                    .hashtagName(tagName)
                                    .build()
                    ));
            post.getHashtags().add(tag);
        }
        post = postRepository.save(post);

        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.POST_CREATED, post, FEED_EVENTS_TOPIC);

        // 엘라스틱 서치 저장 Kafka 이벤트 발행
        PostElasticsearchRequestDto dto = PostElasticsearchRequestDto.fromV2(post);
        elasticsearchKafkaProducerService.sendCommentCreate(dto);

        return post;
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    public Page<PostV2> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    /**
     * 게시글 상세 조회 (views 증가 포함)
     */
    @Transactional
    public PostV2 getPostById(UUID postId) {
        PostV2 post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        // 조회수 버퍼 처리
        bufferView(postId);
        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.POST_VIEWED, post, FEED_EVENTS_TOPIC);
        return post;
    }


    /**
     * 게시글 수정 (내용 업데이트)
     */
    public PostV2 updatePost(UUID postId, PostRequestDtoV2 request, long userId) {
        PostV2 post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));

        if (post.getUserId() != userId) {
            throw new PostDomainException(PostDomainExceptionCode.NOT_OWNER);
        }

        post.updateContent(request.getPostContent());
        post = postRepository.save(post);
        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.POST_UPDATED, post, FEED_EVENTS_TOPIC);
        return post;
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    public void deletePost(UUID postId, long userId) {
        PostV2 post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        if (post.getUserId() != userId) {
            throw new PostDomainException(PostDomainExceptionCode.NOT_OWNER);
        }
        postRepository.delete(post);
        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.POST_DELETED, post, FEED_EVENTS_TOPIC);
    }

    /**
     * 게시글에 해시태그 추가
     */
    public PostV2 addHashtags(UUID postId, List<String> hashtags, long userId) {
        PostV2 post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));

        if (post.getUserId() != userId) {
            throw new PostDomainException(PostDomainExceptionCode.NOT_OWNER);
        }

        for (String hashtagName : hashtags) {
            HashtagV2 hashtag = hashtagRepository.findByHashtagName(hashtagName)
                    .orElseGet(() -> {
                        HashtagV2 newTag = HashtagV2.builder()
                                .hashtagName(hashtagName)
                                .build();
                        return hashtagRepository.save(newTag);
                    });
            post.getHashtags().add(hashtag);
        }
        post = postRepository.save(post);
        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.POST_UPDATED, post, FEED_EVENTS_TOPIC);
        return post;
    }

    /**
     * 게시글 존재 여부 확인
     */
    public boolean exists(UUID postId) {
        return postRepository.existsById(postId);
    }

    /**
     * 댓글 수 증가 (버퍼+비동기) + 기존 피드 이벤트
     */
    @Transactional
    public void addCommentCount(UUID postId) {
        // 1) 피드 서비스용 이벤트 (기존 로직 그대로)
        PostV2 post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        kafkaUtil.sendKafkaEvent(EventType.COMMENT_CREATED, post, FEED_EVENTS_TOPIC);

        // 2) Redis 버퍼에 즉시 +1
        String key = postId.toString();
        redisTemplate.opsForHash().increment(REDIS_COMMENT_COUNT_HASH, key, 1);
        redisTemplate.opsForSet().add(REDIS_COMMENT_DIRTY_SET, key);

        // 3) 비동기 카운트 업데이트용 이벤트 발행
        kafkaUtil.sendCommentCountEvent(
                CommentCountEventDto.builder()
                        .postId(postId)
                        .delta(1)
                        .build()
        );
    }

    /**
     * 댓글 수 감소 (버퍼+비동기) + 기존 피드 이벤트
     */
    @Transactional
    public void minusCommentCount(UUID postId) {
        // 1) 피드 서비스용 이벤트 (기존 로직 그대로)
        PostV2 post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        kafkaUtil.sendKafkaEvent(EventType.COMMENT_DELETED, post, FEED_EVENTS_TOPIC);

        // 2) Redis 버퍼에 즉시 -1
        String key = postId.toString();
        redisTemplate.opsForHash().increment(REDIS_COMMENT_COUNT_HASH, key, -1);
        redisTemplate.opsForSet().add(REDIS_COMMENT_DIRTY_SET, key);

        // 3) 비동기 카운트 업데이트용 이벤트 발행
        kafkaUtil.sendCommentCountEvent(
                CommentCountEventDto.builder()
                        .postId(postId)
                        .delta(-1)
                        .build()
        );
    }


    /**
     * 조회수 버퍼 처리
     */
    public void bufferView(UUID postId) {
        String postIdStr = postId.toString();
        // 조회수 1 증가 (Hash 구조)
        redisTemplate.opsForHash().increment(REDIS_VIEW_COUNT_HASH_KEY, postIdStr, 1);
        // dirty check용 기록
        redisTemplate.opsForSet().add(REDIS_VIEW_DIRTY_KEY_SET, postIdStr);
    }

}
