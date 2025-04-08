package com.team15gijo.post.presentation.dto.v1;


import com.team15gijo.post.domain.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostFeedResponseDto {
    private UUID postId;
    private long userId;
    private String username;
    private String region;
    private String content;
    private List<String> hashtags;
    private int views;
    private int commentCount;
    private int likeCount;
    private double popularityScore;
    private LocalDateTime createdAt;

    public static PostFeedResponseDto from(Post post) {
        return PostFeedResponseDto.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .username(post.getUsername())
                .region(post.getRegion())
                .content(post.getContent())
                .hashtags(post.getHashtags())
                .views(post.getViews())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .popularityScore(post.getPopularityScore())
                .createdAt(post.getCreatedAt())
                .build();
    }
}