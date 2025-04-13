package com.team15gijo.comment.presentation.controller.v1;

import com.team15gijo.comment.application.service.v1.CommentService;
import com.team15gijo.comment.domain.model.Comment;
import com.team15gijo.comment.presentation.dto.v1.CommentRequestDto;
import com.team15gijo.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 엔드포인트
     */
    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<Comment>> createComment(
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Nickname") String username,
            @RequestBody CommentRequestDto request) {
        Comment created = commentService.createComment(userId, username, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글이 성공적으로 작성되었습니다.", created));
    }

    /**
     * 댓글 목록 조회 (페이징)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<Page<Comment>>> getCommentsByPostId(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Comment> comments = commentService.getCommentsByPostId(postId, pageable);
        return ResponseEntity.ok(ApiResponse.success("댓글 목록 조회 성공.", comments));
    }

    /**
     * 댓글 수정 엔드포인트 (내용 업데이트) - 현재 로그인 사용자의 X-User-Id를 추가로 받음
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Comment>> updateComment(
            @PathVariable UUID commentId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CommentRequestDto request) {
        Comment updated = commentService.updateComment(commentId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 성공적으로 수정되었습니다.", updated));
    }

    /**
     * 댓글 삭제 엔드포인트 - 현재 로그인 사용자의 X-User-Id를 추가로 받음
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable UUID commentId,
            @RequestHeader("X-User-Id") Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 성공적으로 삭제되었습니다.", null));
    }
}
