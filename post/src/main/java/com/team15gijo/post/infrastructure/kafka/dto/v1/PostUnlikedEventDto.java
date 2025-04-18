package com.team15gijo.post.infrastructure.kafka.dto.v1;

import com.team15gijo.post.domain.model.v2.PostV2;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUnlikedEventDto implements FeedEventDto {
    @Builder.Default
    private EventType type = EventType.POST_UNLIKED;
    private UUID postId;
    private String region;
    private int delta;
    private int likeCount;

    public static PostUnlikedEventDto from(PostV2 post) {
        return PostUnlikedEventDto.builder()
                .postId(post.getPostId())
                .region(post.getRegion())
                .delta(-1) // default: 1
                .likeCount(post.getLikeCount())
                .build();
    }
}
