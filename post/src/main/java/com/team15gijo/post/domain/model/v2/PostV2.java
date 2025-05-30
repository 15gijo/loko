package com.team15gijo.post.domain.model.v2;

import com.team15gijo.common.model.base.BaseEntity;
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
@SQLDelete(sql = "UPDATE p_posts SET deleted_at = now(), deleted_by = updated_by WHERE post_id = ?")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostV2 extends BaseEntity {

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
    private Set<HashtagV2> hashtags = new HashSet<>();

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
    @Column(name = "popularity_score", nullable = false, columnDefinition = "double precision default 0.0")
    private double popularityScore = 0.0;

    // 콘텐츠 업데이트 메서드
    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("게시글 내용은 비어있을 수 없습니다.");
        }
        this.postContent = newContent;
    }

    // 조회수 증가 메서드 (setter 없이)
    public void incrementViews() {
        this.views++;
    }

    public void increaseViews(Integer viewCount) {
        this.views += viewCount;
    }

    // 댓글 수 증가 메서드 (setter 없이)
    public void incrementCommentCount() {
        this.commentCount++;
    }

    // 댓글 수 감소 메서드
    public void decrementCommentCount() {
        this.commentCount--;
    }


    // 좋아요 증가 메서드 (setter 없이)
    public void incrementLikeCount() {
        this.likeCount++;
    }

    // 좋아요 감소 메서드 (setter 없이)
    public void decrementLikeCount() {
        this.likeCount--;
    }

    // 새로운 게시글 생성 정적 팩토리 메서드
    public static PostV2 createPost(long userId, String username, String region, String postContent) {
        PostV2 post = PostV2.builder()
                .userId(userId)
                .username(username)
                .region(region)
                .postContent(postContent)
                .views(0)
                .commentCount(0)
                .likeCount(0)
                .popularityScore(0.0)
                .build();
        return post;
    }


}
