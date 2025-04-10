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
public class PostViewedEventDto implements FeedEventDto {
    @Builder.Default
    private EventType type = EventType.POST_VIEWED;
    private UUID postId;
    private String region;
    private int delta;
    private int views;
}
