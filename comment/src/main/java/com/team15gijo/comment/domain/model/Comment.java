package com.team15gijo.comment.domain.model;

import com.team15gijo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "comments")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE comments SET is_deleted = true WHERE comment_id = ?")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "comment_id", updatable = false, nullable = false)
    private UUID commentId;

    // 대상 게시글의 ID
    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 대댓글인 경우 상위 댓글 ID, 최상위 댓글은 null
    @Column(name = "parent_comment_id")
    private UUID parentCommentId;
}
