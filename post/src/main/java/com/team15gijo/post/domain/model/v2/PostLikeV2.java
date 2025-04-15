package com.team15gijo.post.domain.model.v2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_post_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "user_id"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeV2 {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "like_id", updatable = false, nullable = false)
    private UUID likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostV2 post;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 정적 팩토리 메서드: PostLike 생성
    public static PostLikeV2 createPostLike(PostV2 post, long userId) {
        return PostLikeV2.builder()
                .post(post)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
