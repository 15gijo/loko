package com.team15gijo.post.domain.model;

import com.team15gijo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "p_posts")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_posts SET deleted_at = now() WHERE post_id = ?")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "post_id", updatable = false, nullable = false)
    private UUID postId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "region", nullable = false, length = 50)
    private String region;

    @Column(name = "post_content", nullable = false, columnDefinition = "TEXT")
    private String postContent;

    @ManyToMany
    @JoinTable(
            name = "p_post_hashtag_map",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    @Builder.Default
    private Set<Hashtag> hashtags = new HashSet<>();

    @Builder.Default
    @Column(name = "views", nullable = false, columnDefinition = "int default 0")
    private int views = 0;

    @Builder.Default
    @Column(name = "comment_count", nullable = false, columnDefinition = "int default 0")
    private int commentCount = 0;

    @Builder.Default
    @Column(name = "like_count", nullable = false, columnDefinition = "int default 0")
    private int likeCount = 0;

    @Builder.Default
    @Column(name = "popularity_score", nullable = false, columnDefinition = "double default 0.0")
    private double popularityScore = 0.0;

    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("게시글 내용은 비어있을 수 없습니다.");
        }
        this.postContent = newContent;
    }
}
