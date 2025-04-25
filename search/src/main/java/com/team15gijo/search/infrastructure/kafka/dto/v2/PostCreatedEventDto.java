package com.team15gijo.search.infrastructure.kafka.dto.v2;

import com.team15gijo.search.domain.model.PostDocument;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreatedEventDto implements EsEventDto {
    @Builder.Default
    private EventType type = EventType.POST_CREATED;
    private UUID postId;
    private Long userId;
    private String username;
    private String region;
    private String postContent;
    private List<String> hashtags;
    private int views;
    private int commentCount;
    private int likeCount;
    private double popularityScore;
    private LocalDateTime createdAt;

    public PostDocument toEntity() {
        return PostDocument.builder()
                .postId(postId)
                .username(username)
                .region(region)
                .postContent(postContent)
                .hashtags(hashtags != null ? new ArrayList<>(hashtags) : new ArrayList<>())
                .views(views)
                .commentCount(commentCount)
                .likeCount(likeCount)
                .createdAt(createdAt)
                .build();
    }
}
