package com.team15gijo.feed.infrastructure.client.post.dto;


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
    private String content;
    private List<String> hashtags;
    private int views;
    private int commentCount;
    private int likeCount;
    private double popularityScore;
    private LocalDateTime createdAt;
}