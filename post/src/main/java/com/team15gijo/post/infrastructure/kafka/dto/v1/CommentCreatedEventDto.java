package com.team15gijo.post.infrastructure.kafka.dto.v1;

import com.team15gijo.post.domain.model.Post;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreatedEventDto implements FeedEventDto {
    @Builder.Default
    private EventType type = EventType.COMMENT_CREATED;
    private UUID postId;
    private String region;
    private int delta;
    private int commentCount;

    public static CommentCreatedEventDto from(Post post) {
        return CommentCreatedEventDto.builder()
                .postId(post.getPostId())
                .region(post.getRegion())
                .delta(1) // default: 1
                .commentCount(post.getCommentCount())
                .build();
    }
}
