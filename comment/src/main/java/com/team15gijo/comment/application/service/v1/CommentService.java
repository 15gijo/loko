package com.team15gijo.comment.application.service.v1;

import com.team15gijo.comment.domain.exception.CommentDomainException;
import com.team15gijo.comment.domain.exception.CommentDomainExceptionCode;
import com.team15gijo.comment.domain.model.Comment;
import com.team15gijo.comment.domain.repository.CommentRepository;
import com.team15gijo.comment.infrastructure.client.PostClient;
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

    /**
     * 댓글 생성: 게시글 존재 여부를 검증한 후 댓글을 생성하고,
     * 게시글에 댓글 수 증가를 호출
     */
    public Comment createComment(long userId, String username, UUID postId, CommentRequestDto request) {
        // Feign Client 호출로 게시글 존재 여부 확인
        ApiResponse<Boolean> existsResponse = postClient.exists(postId);
        if (existsResponse.getData() == null || !existsResponse.getData()) {
            throw new CommentDomainException(CommentDomainExceptionCode.POST_NOT_FOUND);
        }

        // Comment 엔티티의 정적 팩토리 메서드를 활용하여 댓글 생성
        Comment comment = Comment.createComment(postId, userId, username, request.getCommentContent(), request.getParentCommentId());
        Comment savedComment = commentRepository.save(comment);

        // 게시글의 댓글 수 증가 호출
        postClient.addCommentCount(postId);

        return savedComment;
    }

    /**
     * 특정 게시글의 댓글 목록 조회 (페이징)
     */
    public Page<Comment> getCommentsByPostId(UUID postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    /**
     * 댓글 수정 (내용 업데이트)
     */
    public Comment updateComment(UUID commentId, CommentRequestDto request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentDomainException(CommentDomainExceptionCode.COMMENT_NOT_FOUND));
        comment.updateContent(request.getCommentContent());
        return commentRepository.save(comment);
    }

    /**
     * 댓글 삭제
     */
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentDomainException(CommentDomainExceptionCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);
    }
}
