package com.team15gijo.comment.application.service.v2;

import com.team15gijo.comment.domain.exception.CommentDomainException;
import com.team15gijo.comment.domain.exception.CommentDomainExceptionCode;
import com.team15gijo.comment.application.filter.v2.CommentFilter;
import com.team15gijo.comment.domain.model.v2.CommentV2;
import com.team15gijo.comment.domain.repository.v2.CommentRepositoryV2;
import com.team15gijo.comment.infrastructure.client.v2.PostClientV2;
import com.team15gijo.comment.infrastructure.client.v2.ai.ContentModerationClient;
import com.team15gijo.comment.infrastructure.client.v2.ai.ModerationResponseDto;
import com.team15gijo.comment.infrastructure.client.v2.ai.ModerationRequestDto;
import com.team15gijo.comment.infrastructure.kafka.dto.CommentCountEventDto;
import com.team15gijo.comment.infrastructure.kafka.dto.CommentNotificationEventDto;
import com.team15gijo.comment.infrastructure.kafka.service.KafkaProducerService;
import com.team15gijo.comment.presentation.dto.v2.CommentRequestDtoV2;
import com.team15gijo.common.dto.ApiResponse;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceV2 {

    private final CommentRepositoryV2 commentRepository;
    private final PostClientV2 postClient;  // Feign Client 주입
    private final KafkaProducerService producerService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final CommentFilter commentFilter;
    private final ContentModerationClient moderationClient;

    private static final String REDIS_COMMENT_COUNT_HASH_KEY = "comments:buffer";
    private static final String REDIS_COMMENT_DIRTY_KEY_SET  = "comments:dirty-keys";


    /**
     * 댓글 생성: 게시글 존재 여부를 검증한 후 댓글 생성 및 게시글의 댓글 수 증가 호출
     */
    @Transactional
    public CommentV2 createComment(long userId, String username, UUID postId, CommentRequestDtoV2 request) {

        // Feign Client 호출로 게시글 존재 여부 확인
        ApiResponse<Boolean> existsResponse = postClient.exists(postId);
        if (existsResponse.getData() == null || !existsResponse.getData()) {
            throw new CommentDomainException(CommentDomainExceptionCode.POST_NOT_FOUND);
        }


        // 정적 금지어 검사
        commentFilter.validateContent(request.getCommentContent());

        // AI 자동 숨김 검사
        boolean off = moderationClient
                .moderate(new ModerationRequestDto(request.getCommentContent()))
                .isOffensive();



        int depth = 0;
        if (request.getParentCommentId() != null) {
            // 부모 댓글 존재 여부 확인
            CommentV2 parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CommentDomainException(
                            CommentDomainExceptionCode.COMMENT_NOT_FOUND));
            if (!parentComment.getPostId().equals(postId)) {
                throw new CommentDomainException(CommentDomainExceptionCode.COMMENT_NOT_FOUND);
            }
            depth = parentComment.getDepth() + 1;
        }

        CommentV2 comment = CommentV2.createComment(postId, userId, username, request.getCommentContent(), request.getParentCommentId(), depth);
        if (off) comment.markHidden();
        CommentV2 savedComment = commentRepository.save(comment);

        if (!off) {
            // 4) Redis 캐시 즉시 증분
            String key = postId.toString();
            redisTemplate.opsForHash()
                    .increment(REDIS_COMMENT_COUNT_HASH_KEY, key, 1);
            redisTemplate.opsForSet()
                    .add(REDIS_COMMENT_DIRTY_KEY_SET, key);

            // 5) Kafka 이벤트 발행 (카운트 업데이트)
            producerService.sendCommentCount(
                    CommentCountEventDto.builder()
                            .postId(postId)
                            .delta(1)
                            .build()
            );


            // 알림 서버로 카프카 메세지 전송
            producerService.sendCommentCreate(CommentNotificationEventDto.from(
                    request.getReceiverId(),
                    username,
                    request.getCommentContent()));

        }
        return savedComment;
    }

    /**
     * 특정 게시글의 댓글 목록 조회 (페이징)
     */
    public Page<CommentV2> getCommentsByPostId(UUID postId, Pageable pageable) {
        return commentRepository.findByPostIdAndIsHiddenFalse(postId, pageable);
    }

    /**
     * 댓글 수정 (내용 업데이트) - 현재 로그인한 사용자(userId)가 댓글 소유자인지 확인
     */
    public CommentV2 updateComment(UUID commentId, CommentRequestDtoV2 request, long userId) {
        CommentV2 comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentDomainException(CommentDomainExceptionCode.COMMENT_NOT_FOUND));

        if (comment.getUserId() != userId) {
            throw new CommentDomainException(CommentDomainExceptionCode.NOT_OWNER);
        }

        comment.updateContent(request.getCommentContent());
        return commentRepository.save(comment);
    }

    /**
     * 댓글 삭제 - 현재 로그인한 사용자(userId)가 댓글 소유자인지 확인
     */
    @Transactional
    public void deleteComment(UUID commentId, long userId) {
        CommentV2 comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentDomainException(CommentDomainExceptionCode.COMMENT_NOT_FOUND));

        if (comment.getUserId() != userId) {
            throw new CommentDomainException(CommentDomainExceptionCode.NOT_OWNER);
        }

        // (1) 댓글 삭제
        commentRepository.delete(comment);
        UUID postId = comment.getPostId();
        // (2) Redis 즉시 감산
        String key = postId.toString();
        redisTemplate.opsForHash().increment(REDIS_COMMENT_COUNT_HASH_KEY, key, -1);
        redisTemplate.opsForSet().add(REDIS_COMMENT_DIRTY_KEY_SET, key);
        // (3) Kafka로 delta=-1 이벤트 발행
        producerService.sendCommentCount(
                CommentCountEventDto.builder()
                        .postId(postId)
                        .delta(-1)
                        .build()
        );
    }

}
