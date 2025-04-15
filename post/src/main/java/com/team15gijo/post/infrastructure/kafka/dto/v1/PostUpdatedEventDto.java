package com.team15gijo.post.infrastructure.kafka.dto.v1;

import com.team15gijo.post.domain.model.v2.HashtagV2;
import com.team15gijo.post.domain.model.v2.PostV2;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdatedEventDto implements FeedEventDto {
    @Builder.Default
    private EventType type = EventType.POST_UPDATED;
    private UUID postId;
    private Long userId;
    private String username;
    private String region;
    private String postContent;
    private List<String> hashtags;
    private int views;
    private int commentCount;
    private int likeCount;
    private double popularityScore;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public static PostUpdatedEventDto from(PostV2 post) {
        return PostUpdatedEventDto.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .username(post.getUsername())
                .region(post.getRegion())
                .postContent(post.getPostContent())
                .hashtags(
                        post.getHashtags().stream()
                                .map(HashtagV2::getHashtagName) // Hashtag 클래스에 getName() 메서드가 있다고 가정
                                .toList()
                )
                .views(post.getViews())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .popularityScore(post.getPopularityScore())
                .createdAt(post.getCreatedAt())
                .deletedAt(post.getDeletedAt())
                .build();
    }

}
