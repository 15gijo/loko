package com.team15gijo.post.presentation.dto.v2;

import com.team15gijo.post.domain.model.v2.HashtagV2;
import com.team15gijo.post.domain.model.v2.PostV2;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostSearchResponseDtoV2 {

    private UUID postId;
    private String username;
    private String postContent;
    private List<String> hashtags;
    private int views;
    private int commentCount;
    private int likeCount;
    private LocalDateTime createdAt;

    public static PostSearchResponseDtoV2 from(PostV2 post) {
        return PostSearchResponseDtoV2.builder()
                .postId(post.getPostId())
                .username(post.getUsername())
                .postContent(post.getPostContent())
                .hashtags(post.getHashtags().stream()
                        .map(HashtagV2::getHashtagName)
                        .toList())
                .views(post.getViews())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}