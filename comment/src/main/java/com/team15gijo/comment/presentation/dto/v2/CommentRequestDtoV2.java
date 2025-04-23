package com.team15gijo.comment.presentation.dto.v2;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDtoV2 {
    private String commentContent;
    // 대댓글인 경우 상위 댓글 ID (최상위 댓글은 null)
    private UUID parentCommentId;

    // 게시글인 경우 게시글 작성자, 대댓글인 경우 댓글 작성자.
    private Long receiverId;
}
