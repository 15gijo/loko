package com.team15gijo.comment.presentation.controller.v2;


import com.team15gijo.comment.application.service.v2.CommentServiceV2;
import com.team15gijo.comment.domain.model.v2.CommentV2;
import com.team15gijo.comment.presentation.dto.v2.CommentRequestDtoV2;
import com.team15gijo.common.dto.ApiResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/comments")
public class CommentControllerV2 {

    private final CommentServiceV2 commentService;

    /**
     * 댓글 생성 엔드포인트
     */
    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommentV2>> createComment(
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Nickname") String username,
            @RequestBody CommentRequestDtoV2 request) {
        String decodedUsername = URLDecoder.decode(username, StandardCharsets.UTF_8);
        CommentV2 created = commentService.createComment(userId, decodedUsername, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글이 성공적으로 작성되었습니다.", created));
    }

    /**
     * 댓글 목록 조회 (페이징)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<Page<CommentV2>>> getCommentsByPostId(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CommentV2> comments = commentService.getCommentsByPostId(postId, pageable);
        return ResponseEntity.ok(ApiResponse.success("댓글 목록 조회 성공.", comments));
    }

    /**
     * 댓글 수정 엔드포인트 (내용 업데이트) - 현재 로그인 사용자의 X-User-Id를 추가로 받음
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentV2>> updateComment(
            @PathVariable UUID commentId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CommentRequestDtoV2 request) {
        CommentV2 updated = commentService.updateComment(commentId, request, userId);
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
