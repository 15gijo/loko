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
public class PostDeletedEventDto implements FeedEventDto {
    @Builder.Default
    private EventType type = EventType.POST_DELETED;
    private UUID postId;

    public static PostDeletedEventDto from(Post post) {
        return PostDeletedEventDto.builder()
                .postId(post.getPostId())
                .build();
    }
}
