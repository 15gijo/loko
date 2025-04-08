package com.team15gijo.post.domain.model;

import com.team15gijo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_posts")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_posts SET is_deleted = true WHERE post_id = ?")
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

    // 유저의 지역정보
    @Column(name = "region", nullable = false, length = 50)
    private String region;

    @Column(name = "post_content", nullable = false, columnDefinition = "TEXT")
    private String postContent;

    // 해시태그 (추후 자동 생성 로직 추가 예정)
    @ElementCollection
    @CollectionTable(name = "post_hashtags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "hashtag")
    private List<String> hashtags;

    @Column(name = "views")
    private int views;

    // 추가: 댓글 수
    @Column(name = "comment_count")
    private int commentCount;

    // 추가: 좋아요 수
    @Column(name = "like_count")
    private int likeCount;

    // 추가: popularity_score
    @Column(name = "popularity_score")
    private double popularityScore;
}
