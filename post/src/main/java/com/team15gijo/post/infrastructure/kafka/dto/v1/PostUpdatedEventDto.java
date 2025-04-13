package com.team15gijo.post.infrastructure.kafka.dto.v1;

import com.team15gijo.post.domain.model.Hashtag;
import com.team15gijo.post.domain.model.Post;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public static PostUpdatedEventDto from(Post post) {
        return PostUpdatedEventDto.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .username(post.getUsername())
                .region(post.getRegion())
                .postContent(post.getPostContent())
                .hashtags(
                        post.getHashtags().stream()
                                .map(Hashtag::getHashtagName) // Hashtag 클래스에 getName() 메서드가 있다고 가정
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
