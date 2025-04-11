package com.team15gijo.post.application.service.v1;

import com.team15gijo.post.domain.exception.PostDomainException;
import com.team15gijo.post.domain.exception.PostDomainExceptionCode;
import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.domain.model.Hashtag;
import com.team15gijo.post.domain.repository.PostRepository;
import com.team15gijo.post.domain.repository.HashtagRepository;
import com.team15gijo.post.presentation.dto.v1.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        Post post = Post.createPost(userId, username, region, request.getPostContent());
        return postRepository.save(post);
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    /**
     * 게시글 상세 조회 (views 증가 포함)
     */
    @Transactional
    public Post getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        // 도메인 메서드를 통해 조회수 증가
        post.incrementViews();
        return postRepository.save(post);
    }

    /**
     * 게시글 수정 (내용 업데이트) - 수정: 현재 로그인한 사용자(userId)가 게시글 소유자인지 확인
     */
    public Post updatePost(UUID postId, PostRequestDto request, long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));

        if (post.getUserId() != userId) {
            throw new PostDomainException(PostDomainExceptionCode.NOT_OWNER);
        }

        post.updateContent(request.getPostContent());
        return postRepository.save(post);
    }

    /**
     * 게시글 삭제 (Soft Delete) - 삭제: 현재 로그인한 사용자(userId)가 게시글 소유자인지 확인
     */
    public void deletePost(UUID postId, long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));

        if (post.getUserId() != userId) {
            throw new PostDomainException(PostDomainExceptionCode.NOT_OWNER);
        }
        postRepository.delete(post);
    }

    /**
     * 게시글에 해시태그 추가 - 보통은 소유자 검증이 필요할 수 있음(선택 사항)
     */
    public Post addHashtags(UUID postId, List<String> hashtags, long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));

        if (post.getUserId() != userId) {
            throw new PostDomainException(PostDomainExceptionCode.NOT_OWNER);
        }

        for (String hashtagName : hashtags) {
            Hashtag hashtag = hashtagRepository.findByHashtagName(hashtagName)
                    .orElseGet(() -> {
                        Hashtag newTag = Hashtag.builder()
                                .hashtagName(hashtagName)
                                .build();
                        return hashtagRepository.save(newTag);
                    });
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

    /**
     * 게시글의 댓글 수(commentCount)를 증가시키는 메서드
     */
    @Transactional
    public void addCommentCount(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        // 도메인 메서드를 통해 댓글 수 증가
        post.incrementCommentCount();
        postRepository.save(post);
    }
}
