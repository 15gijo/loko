package com.team15gijo.post.presentation.dto.v1;


import com.team15gijo.post.domain.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
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

    public static PostFeedResponseDto from(Post post) {

        /*
         *Hashtag 엔티티의 hashtagName 필드를 추출하여 List<String>으로 변환
         */
        List<String> hashtagNames = post.getHashtags().stream()
                .map(hashtag -> hashtag.getHashtagName())
                .collect(Collectors.toList());


        return PostFeedResponseDto.builder()
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