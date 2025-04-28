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
public class PostDeletedEventDto {
    @Builder.Default
    private EventType type = EventType.POST_DELETED;
    private UUID postId;

}
