package com.team15gijo.post.application.service.v1;

import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.domain.repository.PostRepository;
import com.team15gijo.post.presentation.dto.v1.PostRequest;
import com.team15gijo.post.presentation.dto.v1.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * 게시글 생성
     * (작성자 정보는 추후 인증 로직으로 대체할 수 있습니다.)
     */
    public PostResponse createPost(long userId, String username, String region, PostRequest request) {
        Post post = Post.builder()
                .userId(userId)
                .username(username)
                .region(region)
                .content(request.getContent())
                .hashtags(Collections.emptyList()) // 해시태그 자동 생성 로직 추후 추가 예정
                .views(0)
                .build();
        Post saved = postRepository.save(post);
        return new PostResponse(saved);
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(PostResponse::new);
    }

    /**
     * 게시글 상세 조회
     */
    public PostResponse getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        return new PostResponse(post);
    }

    /**
     * 게시글 수정 (내용 업데이트)
     */
    public PostResponse updatePost(UUID postId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        post.setContent(request.getContent());
        Post updated = postRepository.save(post);
        return new PostResponse(updated);
    }

    /**
     * 게시글 삭제
     */
    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        postRepository.delete(post);
    }
}
