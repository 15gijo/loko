package com.team15gijo.feed.presentation.dto.v1;


import com.team15gijo.feed.domain.model.Feed;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class PostFeedResponseDto {
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

    public static PostFeedResponseDto from(Feed feed) {

        return PostFeedResponseDto.builder()
                .postId(feed.getPostId())
                .userId(feed.getUserId())
                .username(feed.getUsername())
                .region(feed.getRegion())
                .postContent(feed.getPostContent())
                .hashtags(feed.getHashtags())
                .views(feed.getViews())
                .commentCount(feed.getCommentCount())
                .likeCount(feed.getLikeCount())
                .popularityScore(feed.getPopularityScore())
                .createdAt(feed.getCreatedAt())
                .build();
    }
}