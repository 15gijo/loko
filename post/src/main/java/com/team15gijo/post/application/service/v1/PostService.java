package com.team15gijo.post.application.service.v1;

import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.domain.model.Hashtag;
import com.team15gijo.post.domain.repository.PostRepository;
import com.team15gijo.post.domain.repository.HashtagRepository;
import com.team15gijo.post.presentation.dto.v1.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        Post.incrementViews(post);
        return postRepository.save(post);
    }

    /**
     * 게시글 수정 (내용 업데이트)
     */
    public Post updatePost(UUID postId, PostRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        Post.updatePostContent(post, request.getPostContent());
        return postRepository.save(post);
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        postRepository.delete(post);
    }

    /**
     * 게시글에 해시태그 추가
     */
    public Post addHashtags(UUID postId, List<String> hashtags) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        Set<Hashtag> tagsToAdd = new HashSet<>();
        for (String hashtagName : hashtags) {
            Hashtag hashtag = hashtagRepository.findByHashtagName(hashtagName)
                    .orElseGet(() -> hashtagRepository.save(Hashtag.builder()
                            .hashtagName(hashtagName)
                            .build()));
            tagsToAdd.add(hashtag);
        }
        Post.withAdditionalHashtags(post, tagsToAdd);
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        Post.incrementCommentCount(post);
        postRepository.save(post);
    }
}
