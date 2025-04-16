package com.team15gijo.post.presentation.dto.v2;

import com.team15gijo.post.domain.model.v2.PostV2;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDtoV2 {
    private UUID postId;
    private long userId;
    private String username;
    private String region;
    private String postContent;
    private List<String> hashtags;
    private int views;
    private int commentCount;
    private int likeCount;
    private double popularityScore;
    private LocalDateTime createdAt;

    public static PostResponseDtoV2 from(PostV2 post) {

        List<String> hashtagNames = post.getHashtags().stream()
                .map(hashtag -> hashtag.getHashtagName())
                .collect(Collectors.toList());

        return PostResponseDtoV2.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .username(post.getUsername())
                .region(post.getRegion())
                .postContent(post.getPostContent())
                .hashtags(hashtagNames)
                .views(post.getViews())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .popularityScore(post.getPopularityScore())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
