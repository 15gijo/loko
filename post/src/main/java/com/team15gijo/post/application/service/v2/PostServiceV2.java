package com.team15gijo.post.application.service.v2;

import com.team15gijo.post.infrastructure.kafka.util.KafkaUtil;
import com.team15gijo.post.domain.exception.PostDomainException;
import com.team15gijo.post.domain.exception.PostDomainExceptionCode;
import com.team15gijo.post.domain.model.Hashtag;
import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.domain.repository.HashtagRepository;
import com.team15gijo.post.domain.repository.PostRepository;
import com.team15gijo.post.infrastructure.kafka.dto.v1.EventType;
import com.team15gijo.post.presentation.dto.v2.PostRequestDtoV2;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceV2 {

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final KafkaUtil kafkaUtil;

    private static final String FEED_EVENTS_TOPIC = "feed_events";

    /**
     * 게시글 생성
     */
    public Post createPost(long userId, String username, String region, PostRequestDtoV2 request) {
        Post post = Post.createPost(userId, username, region, request.getPostContent());
        post = postRepository.save(post);
        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.POST_CREATED, post, FEED_EVENTS_TOPIC);
        return post;
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    /**
     * 게시글 상세 조회 (views 증가 포함)
     */
    @Transactional
    public Post getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        // 도메인 메서드를 통해 조회수 증가
        post.incrementViews();
        post = postRepository.save(post);
        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.POST_VIEWED, post, FEED_EVENTS_TOPIC);
        return post;
    }


    /**
     * 게시글 수정 (내용 업데이트)
     */
    public Post updatePost(UUID postId, PostRequestDtoV2 request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        post.updateContent(request.getPostContent());
        post = postRepository.save(post);
        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.POST_UPDATED, post, FEED_EVENTS_TOPIC);
        return post;
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        postRepository.delete(post);
        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.POST_DELETED, post, FEED_EVENTS_TOPIC);
    }

    /**
     * 게시글에 해시태그 추가
     */
    public Post addHashtags(UUID postId, List<String> hashtags) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        for (String hashtagName : hashtags) {
            Hashtag hashtag = hashtagRepository.findByHashtagName(hashtagName)
                    .orElseGet(() -> {
                        Hashtag newTag = Hashtag.builder()
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
     * 게시글의 댓글 수(commentCount)를 증가시키는 메서드
     */
    @Transactional
    public void addCommentCount(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        // 도메인 메서드를 통해 댓글 수 증가
        post.incrementCommentCount();
        postRepository.save(post);
        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.COMMENT_CREATED, post, FEED_EVENTS_TOPIC);
    }

    @Transactional
    public void minusCommentCount(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        // 도메인 메서드를 통해 댓글 수 증가
        post.decrementCommentCount();
        postRepository.save(post);
        // kafka 이벤트 발행
        kafkaUtil.sendKafkaEvent(EventType.COMMENT_DELETED, post, FEED_EVENTS_TOPIC);
    }

}
