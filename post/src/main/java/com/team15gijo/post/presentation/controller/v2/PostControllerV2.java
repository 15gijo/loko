package com.team15gijo.post.presentation.controller.v2;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.post.application.service.v2.PostServiceV2;
import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.presentation.dto.v2.PostRequestDtoV2;
import com.team15gijo.post.presentation.dto.v2.PostResponseDtoV2;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/api/v2/posts")
public class PostControllerV2 {

    private final PostServiceV2 postService;

    /**
     * 게시글 생성 엔드포인트
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDtoV2>> createPost(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Nickname") String username,
            @RequestHeader("X-User-Region") String region,
            @RequestBody PostRequestDtoV2 request) {

        // URL 디코딩하여 원래의 한글 문자열로 복원
        String decodedRegion = URLDecoder.decode(region, StandardCharsets.UTF_8);

        Post created = postService.createPost(userId, username, region, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("신규 게시글 등록 성공", PostResponseDtoV2.from(created)));
    }

    /**
     * 게시글 목록 조회 (페이징 및 정렬)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponseDtoV2>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postId") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Post> posts = postService.getPosts(pageable);
        Page<PostResponseDtoV2> dtoPage = posts.map(PostResponseDtoV2::from);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공", dtoPage));
    }

    /**
     * 게시글 상세 조회 시 조회수 증가 (views 증가)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDtoV2>> getPostById(@PathVariable UUID postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 성공", PostResponseDtoV2.from(post)));
    }

    /**
     * 게시글 수정 엔드포인트 (내용 업데이트) - 현재 로그인 사용자의 X-User-Id를 추가로 받음
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDtoV2>> updatePost(
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PostRequestDtoV2 request) {
        Post updated = postService.updatePost(postId, request);
        return ResponseEntity.ok(ApiResponse.success("게시글 수정 성공", PostResponseDtoV2.from(updated)));
    }

    /**
     * 게시글 삭제 엔드포인트 - 현재 로그인 사용자의 X-User-Id를 추가로 받음
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") Long userId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제 성공", null));
    }

    /**
     * 게시글에 해시태그 추가 엔드포인트 - 소유자 검증을 위해 X-User-Id를 추가로 받음 (옵션)
     */
    @PutMapping("/{postId}/hashtags")
    public ResponseEntity<ApiResponse<PostResponseDtoV2>> addHashtags(
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<String> hashtags) {
        Post updatedPost = postService.addHashtags(postId, hashtags, userId);
        return ResponseEntity.ok(ApiResponse.success("해시태그 추가 성공", PostResponseDtoV2.from(updatedPost)));
    }

    /**
     * 게시글 존재 여부 확인 엔드포인트 (Feign Client 호출용)
     */
    @GetMapping("/{postId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> exists(@PathVariable UUID postId) {
        boolean exists = postService.exists(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 존재 여부 확인", exists));
    }

    /**
     * 게시글 댓글 수 증가 엔드포인트 (댓글 서비스 등에서 호출)
     */
    @PostMapping("/{postId}/increment-comment")
    public ResponseEntity<ApiResponse<Void>> AddCommentCount(@PathVariable UUID postId) {
        postService.addCommentCount(postId);
        return ResponseEntity.ok(ApiResponse.success("댓글 수 증가 성공", null));
    }

    @PostMapping("/{postId}/decrement-comment")
    public ResponseEntity<ApiResponse<Void>> minusCommentCount(@PathVariable UUID postId) {
        postService.minusCommentCount(postId);
        return ResponseEntity.ok(ApiResponse.success("댓글 수 감소 성공", null));
    }
}
