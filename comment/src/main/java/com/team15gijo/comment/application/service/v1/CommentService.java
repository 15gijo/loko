package com.team15gijo.comment.application.service.v1;

import com.team15gijo.comment.domain.exception.CommentDomainException;
import com.team15gijo.comment.domain.exception.CommentDomainExceptionCode;
import com.team15gijo.comment.domain.model.Comment;
import com.team15gijo.comment.domain.repository.CommentRepository;
import com.team15gijo.comment.infrastructure.client.PostClient;
import com.team15gijo.comment.infrastructure.kafka.dto.CommentNotificationEventDto;
import com.team15gijo.comment.infrastructure.kafka.service.KafkaProducerService;
import com.team15gijo.comment.presentation.dto.v1.CommentRequestDto;
import com.team15gijo.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostClient postClient;  // Feign Client 주입
    private final KafkaProducerService producerService;

    /**
     * 댓글 생성: 게시글 존재 여부를 검증한 후 댓글 생성 및 게시글의 댓글 수 증가 호출
     */
    public Comment createComment(long userId, String username, UUID postId, CommentRequestDto request) {
        // Feign Client 호출로 게시글 존재 여부 확인
        ApiResponse<Boolean> existsResponse = postClient.exists(postId);
        if (existsResponse.getData() == null || !existsResponse.getData()) {
            throw new CommentDomainException(CommentDomainExceptionCode.POST_NOT_FOUND);
        }

        Comment comment = Comment.createComment(postId, userId, username, request.getCommentContent(), request.getParentCommentId());
        Comment savedComment = commentRepository.save(comment);

        // 게시글의 댓글 수 증가 호출
        postClient.addCommentCount(postId);

        // 알림 서버로 카프카 메세지 전송
        producerService.sendCommentCreate(CommentNotificationEventDto.from(
                request.getReceiverId(),
                username,
                request.getCommentContent()));

        return savedComment;
    }

    /**
     * 특정 게시글의 댓글 목록 조회 (페이징)
     */
    public Page<Comment> getCommentsByPostId(UUID postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    /**
     * 댓글 수정 (내용 업데이트) - 현재 로그인한 사용자(userId)가 댓글 소유자인지 확인
     */
    public Comment updateComment(UUID commentId, CommentRequestDto request, long userId) {
        Comment comment = commentRepository.findById(commentId)
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
    public void deleteComment(UUID commentId, long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentDomainException(CommentDomainExceptionCode.COMMENT_NOT_FOUND));

        if (comment.getUserId() != userId) {
            throw new CommentDomainException(CommentDomainExceptionCode.NOT_OWNER);
        }

        commentRepository.delete(comment);

        // 댓글 삭제 후 게시글 댓글 수 감소 처리
        postClient.decreaseCommentCount(comment.getPostId());
    }
}
