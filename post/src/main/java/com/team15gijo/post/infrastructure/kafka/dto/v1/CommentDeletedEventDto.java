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
public class CommentDeletedEventDto implements FeedEventDto {
    @Builder.Default
    private EventType type = EventType.COMMENT_DELETED;
    private UUID postId;
    private String region;
    private int delta;
    private int commentCount;

    public static CommentDeletedEventDto from(PostV2 post) {
        return CommentDeletedEventDto.builder()
                .postId(post.getPostId())
                .region(post.getRegion())
                .delta(1) // default: 1
                .commentCount(post.getCommentCount())
                .build();
    }
}
