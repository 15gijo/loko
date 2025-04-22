package com.team15gijo.post.infrastructure.kafka.dto.v2;

import com.team15gijo.post.domain.model.v1.Hashtag;
import com.team15gijo.post.domain.model.v1.Post;
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
public class PostElasticsearchRequestDto {

    private UUID postId;
    private String username;
    private String postContent;
    private List<String> hashtags;
    private String region;

//    private int views;
//    private int commentCount;
//    private int likeCount;
    private LocalDateTime createdAt;

    public static PostElasticsearchRequestDto fromV1(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }
        return PostElasticsearchRequestDto.builder()
                .postId(post.getPostId())
                .username(post.getUsername())
                .postContent(post.getPostContent())
                .hashtags(
                        post.getHashtags() != null ?
                                post.getHashtags().stream()
                                .map(Hashtag::getHashtagName)
                                .toList()
                                : List.of()
                )
                .region(post.getRegion())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static PostElasticsearchRequestDto fromV2(PostV2 post) {
        if (post == null) {
            throw new IllegalArgumentException("PostV2 cannot be null");
        }
        return PostElasticsearchRequestDto.builder()
                .postId(post.getPostId())
                .username(post.getUsername())
                .postContent(post.getPostContent())
                .hashtags(
                        post.getHashtags() != null ?
                                post.getHashtags().stream()
                                        .map(HashtagV2::getHashtagName)
                                        .toList()
                                : List.of()
                )
                .region(post.getRegion())
                .createdAt(post.getCreatedAt())
                .build();
    }
}