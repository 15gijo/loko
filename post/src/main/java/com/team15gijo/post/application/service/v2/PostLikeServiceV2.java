package com.team15gijo.post.application.service.v2;


import com.team15gijo.post.domain.exception.PostDomainException;
import com.team15gijo.post.domain.exception.PostDomainExceptionCode;
import com.team15gijo.post.domain.exception.PostLikeDomainException;
import com.team15gijo.post.domain.model.v2.PostLikeV2;
import com.team15gijo.post.domain.model.v2.PostV2;
import com.team15gijo.post.domain.repository.v2.PostLikeRepositoryV2;
import com.team15gijo.post.domain.repository.v2.PostRepositoryV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostLikeServiceV2 {

    private final PostRepositoryV2 postRepository;
    private final PostLikeRepositoryV2 postLikeRepository;


    /**
     * 게시글 좋아요 추가
     */
    @Transactional
    public PostLikeV2 addLike(UUID postId, long userId) {
        PostV2 post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));

        postLikeRepository.findByPostAndUserId(post, userId).ifPresent(like -> {
            throw new PostLikeDomainException(PostDomainExceptionCode.ALREADY_LIKED);
        });

        PostLikeV2 like = PostLikeV2.createPostLike(post, userId);
        PostLikeV2 savedLike = postLikeRepository.save(like);

        // 좋아요 개수 증가 및 저장
        post.incrementLikeCount();
        postRepository.save(post);

        return savedLike;
    }

    /**
     * 게시글 좋아요 취소
     */
    @Transactional
    public void removeLike(UUID postId, long userId) {
        PostV2 post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));

        PostLikeV2 like = postLikeRepository.findByPostAndUserId(post, userId)
                .orElseThrow(() -> new PostLikeDomainException(PostDomainExceptionCode.LIKE_NOT_FOUND));
        postLikeRepository.delete(like);

        // 좋아요 개수 감소 및 저장
        post.decrementLikeCount();
        postRepository.save(post);

    }

    /**
     * 게시글 좋아요 유저 목록 조회
     */
    public List<PostLikeV2> getLikesForPost(UUID postId) {
        PostV2 post = postRepository.findById(postId)
                .orElseThrow(() -> new PostDomainException(PostDomainExceptionCode.POST_NOT_FOUND));
        return postLikeRepository.findByPost(post);
    }

    /**
     * 유저별 좋아요 목록 조회
     */
    public List<PostLikeV2> getLikesForUser(long userId) {
        return postLikeRepository.findByUserId(userId);
    }
}
