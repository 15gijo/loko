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
public class PostViewedEventDto implements FeedEventDto {
    @Builder.Default
    private EventType type = EventType.POST_VIEWED;
    private UUID postId;
    private String region;
    private int delta;
    private int views;

    public static PostViewedEventDto from(Post post) {
        return PostViewedEventDto.builder()
                .postId(post.getPostId())
                .region(post.getRegion())
                .delta(1) //기본 : 1 => 매 조회 시마다 이벤트 발생 가정
                .views(post.getViews())
                .build();
    }
}
