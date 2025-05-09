package com.team15gijo.post.presentation.dto.v1;

import com.team15gijo.post.domain.model.v1.Hashtag;
import com.team15gijo.post.domain.model.v1.Post;
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
public class PostSearchResponseDto {

    private UUID postId;
    private String username;
    private String postContent;
    private List<String> hashtags;
    private int views;
    private int commentCount;
    private int likeCount;
    private LocalDateTime createdAt;

    public static PostSearchResponseDto from(Post post) {
        return PostSearchResponseDto.builder()
                .postId(post.getPostId())
                .username(post.getUsername())
                .postContent(post.getPostContent())
                .hashtags(post.getHashtags().stream()
                        .map(Hashtag::getHashtagName)
                        .toList())
                .views(post.getViews())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}