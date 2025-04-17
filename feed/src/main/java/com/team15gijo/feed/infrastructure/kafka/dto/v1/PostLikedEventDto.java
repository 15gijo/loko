package com.team15gijo.feed.infrastructure.kafka.dto.v1;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikedEventDto implements FeedEventDto {
    @Builder.Default
    private EventType type = EventType.POST_VIEWED;
    private UUID postId;
    private String region;
    private int delta;
    private int likeCount;
}
