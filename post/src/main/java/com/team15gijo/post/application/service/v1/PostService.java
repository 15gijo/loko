package com.team15gijo.post.application.service.v1;

import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.domain.repository.PostRepository;
import com.team15gijo.post.presentation.dto.v1.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * 게시글 생성
     * (작성자 정보는 현재 파라미터(userId)를 createdBy로, 현재 시간을 createdAt으로 할당하며, 추후 인증 로직 도입 시 변경 예정입니다.)
     */
    public Post createPost(long userId, String username, String region, PostRequestDto request) {
        Post post = Post.builder()
                .userId(userId)
                .username(username)
                .region(region)
                .content(request.getContent())
                .hashtags(Collections.emptyList()) // 해시태그 자동 생성 로직 추후 추가 예정
                .views(0)
                .commentCount(0)      // 기본 댓글 수 0
                .likeCount(0)         // 기본 좋아요 수 0
                .popularityScore(0.0) // 기본 popularity_score 0.0
                // 지금은 파라미터로 받은 userId를 createdBy로, 현재 시간을 createdAt으로 직접 할당합니다.
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();
        return postRepository.save(post);
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    /**
     * 게시글 상세 조회
     */
    public Post getPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
    }

    /**
     * 게시글 수정 (내용 업데이트)
     */
    public Post updatePost(UUID postId, PostRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        post.setContent(request.getContent());
        // JPA Auditing 또는 수동 업데이트에 의해 updatedAt, updatedBy가 기록됩니다.
        return postRepository.save(post);
    }

    /**
     * 게시글 삭제
     */
    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        // JPA Auditing에 의해 deletedAt, deletedBy가 기록되거나, 직접 delete()를 호출합니다.
        postRepository.delete(post);
    }
}
