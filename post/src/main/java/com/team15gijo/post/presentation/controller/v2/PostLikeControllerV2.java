package com.team15gijo.post.presentation.controller.v2;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.post.application.service.v2.PostLikeServiceV2;
import com.team15gijo.post.domain.model.v2.PostLikeV2;
import com.team15gijo.post.presentation.dto.v2.PostLikeResponseDtoV2;
import com.team15gijo.post.presentation.dto.v2.PostLikeListResponseDtoV2;
import com.team15gijo.post.presentation.dto.v2.UserLikeResponseDtoV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/posts")
public class PostLikeControllerV2 {

    private final PostLikeServiceV2 postLikeServiceV2;

    /**
     * 게시글 좋아요 추가
     */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponseDtoV2>> addLike(
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") Long userId) {

        PostLikeV2 like = postLikeServiceV2.addLike(postId, userId);
        return ResponseEntity.ok(ApiResponse.success("좋아요가 성공적으로 추가되었습니다.", PostLikeResponseDtoV2.from(like)));
    }

    /**
     * 게시글 좋아요 취소
     */
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> removeLike(
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") Long userId) {

        postLikeServiceV2.removeLike(postId, userId);
        return ResponseEntity.ok(ApiResponse.success("좋아요가 성공적으로 취소되었습니다.", null));
    }

    /**
     * 게시글 좋아요 유저 목록 조회
     */
    @GetMapping("/{postId}/postlikes")
    public ResponseEntity<ApiResponse<PostLikeListResponseDtoV2>> getLikesByPost(
            @PathVariable UUID postId) {

        List<PostLikeV2> likes = postLikeServiceV2.getLikesForPost(postId);
        return ResponseEntity.ok(ApiResponse.success("좋아요 목록 조회 성공.", PostLikeListResponseDtoV2.from(postId, likes)));
    }

    /**
     * 유저별 좋아요 목록 조회
     */
    @GetMapping("/userlikes")
    public ResponseEntity<ApiResponse<UserLikeResponseDtoV2>> getLikesByUser(
            @RequestHeader("X-User-Id") Long userId) {

        List<UUID> likedPostIds = postLikeServiceV2.getLikesForUser(userId)
                .stream()
                .map(like -> like.getPost().getPostId())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("좋아요 목록 조회 성공.", UserLikeResponseDtoV2.builder()
                .userId(userId)
                .postIds(likedPostIds)
                .build()));
    }
}
