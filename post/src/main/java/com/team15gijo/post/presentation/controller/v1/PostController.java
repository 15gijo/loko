package com.team15gijo.post.presentation.controller.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.post.application.service.v1.PostService;
import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.presentation.dto.v1.PostRequestDto;
import com.team15gijo.post.presentation.dto.v1.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    /**
     * 게시글 생성 엔드포인트
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @RequestParam long userId,
            @RequestParam String username,
            @RequestParam String region,
            @RequestBody PostRequestDto request) {
        Post created = postService.createPost(userId, username, region, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("신규 게시글 등록 성공", PostResponseDto.from(created)));
    }

    /**
     * 게시글 목록 조회 (페이징 및 정렬)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponseDto>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postId") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Post> posts = postService.getPosts(pageable);
        Page<PostResponseDto> dtoPage = posts.map(PostResponseDto::from);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공", dtoPage));
    }

    /**
     * 게시글 상세 조회 시 조회수 증가 (views 증가)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPostById(@PathVariable UUID postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 성공", PostResponseDto.from(post)));
    }

    /**
     * 게시글 수정 엔드포인트 (내용 업데이트)
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(@PathVariable UUID postId,
            @RequestBody PostRequestDto request) {
        Post updated = postService.updatePost(postId, request);
        return ResponseEntity.ok(ApiResponse.success("게시글 수정 성공", PostResponseDto.from(updated)));
    }

    /**
     * 게시글 삭제 엔드포인트
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제 성공", null));
    }

    /**
     * 게시글에 해시태그 추가 엔드포인트
     * 예: request body로 ["#Spring", "#Java"] 같은 문자열 배열을 받음
     */
    @PutMapping("/{postId}/hashtags")
    public ResponseEntity<ApiResponse<PostResponseDto>> addHashtags(@PathVariable UUID postId,
            @RequestBody List<String> hashtags) {
        Post updatedPost = postService.addHashtags(postId, hashtags);
        return ResponseEntity.ok(ApiResponse.success("해시태그 추가 성공", PostResponseDto.from(updatedPost)));
    }

    /**
     * 게시글 존재 여부 확인 엔드포인트 (Feign Client에서 호출할 API)
     */
    @GetMapping("/{postId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> exists(@PathVariable UUID postId) {
        boolean exists = postService.exists(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 존재 여부 확인", exists));
    }
}
