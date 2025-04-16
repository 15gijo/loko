package com.team15gijo.comment.presentation.dto.v1;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDto {
    private String commentContent;
    // 대댓글인 경우 상위 댓글 ID (최상위 댓글은 null)
    private UUID parentCommentId;

    // 게시글인 경우 게시글 작성자, 대댓글인 경우 댓글 작성자.
    private Long receiverId;
}
