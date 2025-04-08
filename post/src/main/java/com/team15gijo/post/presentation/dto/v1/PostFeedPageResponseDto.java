package com.team15gijo.post.presentation.dto.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostFeedPageResponseDto {
    private List<PostFeedResponseDto> posts;
    private LocalDateTime nextCursor;

    public static PostFeedPageResponseDto of(List<PostFeedResponseDto> posts) {
        LocalDateTime nextCursor = posts.isEmpty() ? null : posts.get(posts.size() - 1).getCreatedAt();
        return new PostFeedPageResponseDto(posts, nextCursor);
    }
}