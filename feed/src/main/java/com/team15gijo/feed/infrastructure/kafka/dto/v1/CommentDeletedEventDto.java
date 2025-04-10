package com.team15gijo.feed.infrastructure.kafka.dto.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDeletedEventDto implements FeedEventDto {
    @Builder.Default
    private EventType type = EventType.COMMENT_DELETED;
    private UUID postId;
    private String region;
    private int delta;
    private int commentCount;
}
