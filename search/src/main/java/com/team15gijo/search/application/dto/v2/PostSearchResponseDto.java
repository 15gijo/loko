package com.team15gijo.search.application.dto.v2;

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
public class PostSearchResponseDto {

    private UUID postId;
    private String username;
    private String postContent;
    private List<String> hashtags;
    private String region;
    private int views;
    private int commentCount;
    private int likeCount;
    private LocalDateTime createdAt;

    public static PostSearchResponseDto from(PostDocument postDocument) {
        return PostSearchResponseDto.builder()
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
