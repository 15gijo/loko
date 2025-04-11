package com.team15gijo.post.presentation.dto.v2;

import com.team15gijo.post.domain.model.Hashtag;
import com.team15gijo.post.domain.model.Post;
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

    public static PostSearchResponseDtoV2 from(Post post) {
        return PostSearchResponseDtoV2.builder()
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