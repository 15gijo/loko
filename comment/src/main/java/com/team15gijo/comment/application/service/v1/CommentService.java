package com.team15gijo.comment.application.service.v1;

import com.team15gijo.comment.domain.model.Comment;
import com.team15gijo.comment.domain.repository.CommentRepository;
import com.team15gijo.comment.presentation.dto.v1.CommentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * 댓글 생성
     * (작성자 정보는 파라미터로 전달하며, BaseEntity의 createdBy, createdAt 은 빌드 후 설정)
     */
    public Comment createComment(long userId, String username, UUID postId, CommentRequestDto request) {
        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .username(username)
                .commentContent(request.getCommentContent())
                .parentCommentId(request.getParentCommentId())
                .build();
        comment.setCreatedBy(userId);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));
        comment.setCommentContent(request.getCommentContent());
        // JPA Auditing에 의해 updatedAt, updatedBy가 추후 기록될 예정
        return commentRepository.save(comment);
    }

    /**
     * 댓글 삭제
     */
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));
        // JPA Auditing에 의해 deletedAt, deletedBy가 추후 기록될 예정
        commentRepository.delete(comment);
    }
}
