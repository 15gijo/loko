package com.team15gijo.search.infrastructure.kafka.dto.v1;

import com.team15gijo.search.domain.model.PostDocument;
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
    private int views;
    private int commentCount;
    private int likeCount;
    private LocalDateTime createdAt;

    public static PostElasticsearchRequestDto from(PostDocument postDocument) {
        return PostElasticsearchRequestDto.builder()
                .postId(postDocument.getPostId())
                .username(postDocument.getUsername())
                .postContent(postDocument.getPostContent())
                .hashtags(postDocument.getHashtags())
                .region(postDocument.getRegion())
                .views(postDocument.getViews())
                .commentCount(postDocument.getCommentCount())
                .likeCount(postDocument.getLikeCount())
                .createdAt(postDocument.getCreatedAt())
                .build();
    }
}
