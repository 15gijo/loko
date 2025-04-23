package com.team15gijo.comment.domain.model.v2;


import com.team15gijo.common.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_comments")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_comments SET deleted_at = now(),deleted_by = updated_by WHERE comment_id = ?")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentV2 extends BaseEntity {

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

    // 댓글의 중첩(depth): 최상위 댓글은 0, 대댓글은 부모의 depth + 1
    @Column(name = "depth")
    private int depth;

    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden;

    /**
     * 댓글 내용을 업데이트합니다.
     */
    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }
        this.commentContent = newContent;
    }

    public void markHidden() {
        this.isHidden = true;
    }


    /**
     * 정적 팩토리 메서드: 댓글 객체를 생성하고 생성 메타데이터(생성자 ID, 생성일시)를 설정합니다.
     */
    public static CommentV2 createComment(UUID postId, long userId, String username, String commentContent, UUID parentCommentId, int depth) {
        CommentV2 comment = CommentV2.builder()
                .postId(postId)
                .userId(userId)
                .username(username)
                .commentContent(commentContent)
                .parentCommentId(parentCommentId)
                .depth(depth)
                .isHidden(false)
                .build();
        return comment;
    }

}
