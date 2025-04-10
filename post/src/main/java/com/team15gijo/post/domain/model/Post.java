package com.team15gijo.post.domain.model;

import com.team15gijo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
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

    /**
     * 다대다 관계 설정: 연결 테이블(p_post_hashtag_map)을 사용하여 post_id와 hashtag_id 매핑.
     */
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
    @Column(name = "popularity_score", nullable = false, columnDefinition = "double precision default 0.0")
    private double popularityScore = 0.0;

    /**
     * 정적 팩토리 메서드: 새로운 게시글 객체를 생성하고 생성자 메타데이터(생성자 ID, 생성일시)를 설정합니다.
     */
    public static Post createPost(long userId, String username, String region, String postContent) {
        Post post = Post.builder()
                .userId(userId)
                .username(username)
                .region(region)
                .postContent(postContent)
                .views(0)
                .commentCount(0)
                .likeCount(0)
                .popularityScore(0.0)
                .build();
        post.setCreatedBy(userId);
        post.setCreatedAt(LocalDateTime.now());
        return post;
    }

    /**
     * 정적 메서드: 게시글 내용을 업데이트합니다.
     */
    public static Post updatePostContent(Post post, String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("게시글 내용은 비어있을 수 없습니다.");
        }
        post.setPostContent(newContent);
        return post;
    }

    /**
     * 정적 메서드: 조회수를 증가시킵니다.
     */
    public static Post incrementViews(Post post) {
        post.setViews(post.getViews() + 1);
        return post;
    }

    /**
     * 정적 메서드: 댓글 수를 증가시킵니다.
     */
    public static Post incrementCommentCount(Post post) {
        post.setCommentCount(post.getCommentCount() + 1);
        return post;
    }

    /**
     * 정적 메서드: 추가 해시태그들을 게시글에 포함시킵니다.
     */
    public static Post withAdditionalHashtags(Post post, Set<Hashtag> newHashtags) {
        post.getHashtags().addAll(newHashtags);
        return post;
    }
}
