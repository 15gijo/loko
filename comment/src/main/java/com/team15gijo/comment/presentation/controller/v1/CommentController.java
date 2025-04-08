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
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 엔드포인트
     * (userId와 username은 추후 인증 로직 도입 시 JWT 등으로 대체)
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Comment>> createComment(
            @PathVariable UUID postId,
            @RequestParam long userId,
            @RequestParam String username,
            @RequestBody CommentRequestDto request) {
        Comment created = commentService.createComment(userId, username, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글이 성공적으로 작성되었습니다.", created));
    }

    /**
     * 댓글 목록 조회 (페이징)
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Page<Comment>>> getCommentsByPostId(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 페이지 번호를 0-indexed로 변환
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Comment> comments = commentService.getCommentsByPostId(postId, pageable);
        return ResponseEntity.ok(ApiResponse.success("댓글 목록 조회 성공.", comments));
    }

    /**
     * 댓글 수정 엔드포인트 (내용 업데이트)
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Comment>> updateComment(
            @PathVariable UUID commentId,
            @RequestBody CommentRequestDto request) {
        Comment updated = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(ApiResponse.success("댓글이 성공적으로 수정되었습니다.", updated));
    }

    /**
     * 댓글 삭제 엔드포인트
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 성공적으로 삭제되었습니다.", null));
    }
}
