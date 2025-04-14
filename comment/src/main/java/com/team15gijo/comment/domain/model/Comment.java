package com.team15gijo.comment.domain.model;

import com.team15gijo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_comments")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_comments SET deleted_at = now(),deleted_by = updated_by WHERE comment_id = ?")
@Getter
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

    @Column(name = "comment_content", nullable = false, columnDefinition = "TEXT")
    private String commentContent;

    // 대댓글인 경우 상위 댓글 ID, 최상위 댓글은 null
    @Column(name = "parent_comment_id")
    private UUID parentCommentId;

    /**
     * 인스턴스 메서드: 댓글 내용을 업데이트합니다.
     */
    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }
        this.commentContent = newContent;
    }

    /**
     * 정적 팩토리 메서드: 댓글 객체를 생성하고 생성 메타데이터(생성자 ID, 생성일시)를 설정합니다.
     */
    public static Comment createComment(UUID postId, long userId, String username, String commentContent, UUID parentCommentId) {
        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .username(username)
                .commentContent(commentContent)
                .parentCommentId(parentCommentId)
                .build();
        return comment;
    }

}
