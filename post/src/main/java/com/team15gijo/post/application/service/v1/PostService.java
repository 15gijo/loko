package com.team15gijo.post.application.service.v1;

import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.domain.model.Hashtag;
import com.team15gijo.post.domain.repository.PostRepository;
import com.team15gijo.post.domain.repository.HashtagRepository;
import com.team15gijo.post.presentation.dto.v1.PostRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;

    /**
     * 게시글 생성
     */
    public Post createPost(long userId, String username, String region, PostRequestDto request) {
        Post post = Post.builder()
                .userId(userId)
                .username(username)
                .region(region)
                .postContent(request.getPostContent())
                // ManyToMany 관계에서 해시태그는 여기서 비어 있는 상태로 시작
                .build();

        // 기본값 세팅
        post.setViews(0);
        post.setCommentCount(0);
        post.setLikeCount(0);
        post.setPopularityScore(0.0);

        // 빌더 체인에서 상속된 createdBy, createdAt 필드를 설정
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
     * 게시글 상세 조회 및 조회수(views) 증가 처리
     */
    @Transactional
    public Post getPostById(UUID postId) {
        // 게시글 조회 (없으면 예외 발생)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        // 조회수 증가
        post.setViews(post.getViews() + 1);
        // 변경된 게시글을 저장(변경 감지가 활성화되어 있으면 save() 없이도 커밋 시 자동 반영될 수 있음)
        return postRepository.save(post);
    }

    /**
     * 게시글 수정 (내용 업데이트)
     */
    public Post updatePost(UUID postId, PostRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        // 엔티티 내부의 메서드로 업데이트 로직 위임
        post.updateContent(request.getPostContent());
        return postRepository.save(post);
    }


    /**
     * 게시글 삭제 (Soft Delete)
     */
    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        postRepository.delete(post); // @SQLDelete 로직에 의해 deleted_at = now() 로 업데이트
    }

    /**
     * 게시글에 해시태그 추가
     * - 클라이언트로부터 해시태그 문자열 리스트(List<String>)를 받는다.
     * - 이미 존재하는 해시태그인지 검사 후 없으면 새로 생성.
     * - 게시글의 Set<Hashtag>에 추가하고, 저장.
     */
    public Post addHashtags(UUID postId, List<String> hashtags) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        for (String hashtagName : hashtags) {
            Hashtag hashtag = hashtagRepository.findByHashtagName(hashtagName)
                    .orElseGet(() -> {
                        // DB에 없는 해시태그라면 새로 생성
                        Hashtag newTag = Hashtag.builder()
                                .hashtagName(hashtagName)
                                .build();
                        return hashtagRepository.save(newTag);
                    });

            // 중복 추가를 막기 위해 Set을 사용(자동으로 중복 제거 가능)
            post.getHashtags().add(hashtag);
        }

        return postRepository.save(post);
    }

    /**
     * 게시글 존재 여부 확인
     */
    public boolean exists(UUID postId) {
        return postRepository.existsById(postId);
    }
}
