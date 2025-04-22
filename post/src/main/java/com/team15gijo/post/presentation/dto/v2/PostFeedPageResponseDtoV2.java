package com.team15gijo.post.presentation.dto.v2;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostFeedPageResponseDtoV2 {
    private List<PostFeedResponseDtoV2> posts;
    private LocalDateTime nextCursor;

    public static PostFeedPageResponseDtoV2 of(List<PostFeedResponseDtoV2> posts) {
        LocalDateTime nextCursor = posts.isEmpty() ? null : posts.get(posts.size() - 1).getCreatedAt();
        return new PostFeedPageResponseDtoV2(posts, nextCursor);
    }
}