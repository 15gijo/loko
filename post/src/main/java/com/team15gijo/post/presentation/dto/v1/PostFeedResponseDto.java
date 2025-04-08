package com.team15gijo.post.presentation.dto.v1;


import com.team15gijo.post.domain.model.Post;
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
//                .createdAt(post.getCreatedAt())
                .build();
    }
}