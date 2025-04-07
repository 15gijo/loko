package com.team15gijo.post.presentation.controller.v1;

import com.team15gijo.post.application.service.v1.PostService;
import com.team15gijo.post.presentation.dto.v1.PostRequest;
import com.team15gijo.post.presentation.dto.v1.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    /**
     * 게시글 생성 엔드포인트
     * (userId, username, region은 추후 인증 로직으로 대체)
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestParam long userId,
            @RequestParam String username,
            @RequestParam String region,
            @RequestBody PostRequest request) {
        PostResponse response = postService.createPost(userId, username, region, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 목록 조회 (페이징 및 정렬)
     */
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postId") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<PostResponse> responses = postService.getPosts(pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable UUID postId) {
        PostResponse response = postService.getPostById(postId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 수정 엔드포인트 (내용 업데이트)
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable UUID postId,
            @RequestBody PostRequest request) {
        PostResponse response = postService.updatePost(postId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 삭제 엔드포인트
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }
}
