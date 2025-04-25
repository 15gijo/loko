package com.team15gijo.search.infrastructure.kafka.dto.v2;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDeletedEventDto implements EsEventDto {
    @Builder.Default
    private EventType type = EventType.COMMENT_DELETED;
    private UUID postId;
    private String region;
    private int delta;
    private int commentCount;
}

