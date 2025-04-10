package com.team15gijo.feed.domain.model;

import com.team15gijo.common.base.BaseEntity;
import com.team15gijo.feed.infrastructure.converter.StringListConverter;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostUpdatedEventDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_feeds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_feeds SET deleted_at = now(), deleted_by = updated_by WHERE post_id = ?")
public class Feed extends BaseEntity {
    @Id
    @Column(name = "post_id", nullable = false, updatable = false)
    private UUID postId;
    private Long userId;
    private String username;
    private String region;
    private String postContent;

    @Column(name = "hashtags", columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> hashtags = new ArrayList<>();

    private int views;
    private int commentCount;
    private int likeCount;
    private double popularityScore;
    private LocalDateTime createdAtOrigin; //원본 DB의 createdAt 복사본
    private LocalDateTime deletedAtOrigin; //원본 DB의 createdAt 복사본

    public void updateFeed(PostUpdatedEventDto dto) {
        this.userId = dto.getUserId();
        this.username = dto.getUsername();
        this.region = dto.getRegion();
        this.postContent = dto.getPostContent();
        this.hashtags = dto.getHashtags();
        this.views = dto.getViews();
        this.commentCount = dto.getCommentCount();
        this.likeCount = dto.getLikeCount();
        this.popularityScore = dto.getPopularityScore();
        this.createdAtOrigin = dto.getCreatedAt();
        this.deletedAtOrigin = dto.getDeletedAt();
    }
}
