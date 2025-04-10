package com.team15gijo.feed.infrastructure.kafka.dto.v1;

import com.team15gijo.feed.domain.model.Feed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreatedEventDto implements FeedEventDto {
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
    private LocalDateTime deletedAt;

    public Feed toEntity() {
        return new Feed(
                postId,
                userId,
                username,
                region,
                postContent,
                hashtags != null ? new ArrayList<>(hashtags) : new ArrayList<>(),
                views,
                commentCount,
                likeCount,
                popularityScore,
                createdAt,
                deletedAt
        );
    }
}
