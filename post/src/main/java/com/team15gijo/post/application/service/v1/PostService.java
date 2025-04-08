package com.team15gijo.post.application.service.v1;

import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.domain.repository.PostRepository;
import com.team15gijo.post.presentation.dto.v1.PostRequestDto;
import java.util.ArrayList;
import java.util.List;
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
                .postContent(request.getPostContent())
                .hashtags(Collections.emptyList()) // 해시태그 자동 생성 로직 추후 추가 예정
                .views(0)
                .commentCount(0)      // 기본 댓글 수 0
                .likeCount(0)         // 기본 좋아요 수 0
                .popularityScore(0.0) // 기본 popularity_score 0.0
                .build();
        // 빌더 체인에서 상속된 createdBy, createdAt 필드를 설정할 수 없으므로,
        // 빌드 후 setter를 호출하여 값을 할당합니다.
        post.setCreatedBy(userId);
        post.setCreatedAt(LocalDateTime.now());
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
        post.setPostContent(request.getPostContent());
        // JPA Auditing 에 의해 updatedAt, updatedBy가 추후 기록예정
        return postRepository.save(post);
    }

    /**
     * 게시글 삭제
     */
    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        // JPA Auditing에 의해 deletedAt, deletedBy가 추후 기록예정.
        postRepository.delete(post);
    }

    /**
     * 게시글에 해시태그 추가
     * @param postId 해시태그를 추가할 게시글의 ID
     * @param hashtags 추가할 해시태그 리스트
     * @return 해시태그가 추가된 게시글
     */
    public Post addHashtags(UUID postId, List<String> hashtags) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 기존 해시태그 리스트가 를 새 ArrayList로 교체합니다.
        List<String> currentHashtags = post.getHashtags();
        if (currentHashtags == null || currentHashtags.isEmpty()) {
            currentHashtags = new ArrayList<>();
            post.setHashtags(currentHashtags);
        }

        currentHashtags.addAll(hashtags);
        return postRepository.save(post);
    }
}
