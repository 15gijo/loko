package com.team15gijo.comment.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentNotificationEventDto {
    private Long receiverId;
    private String nickname;
    private String commentContent;

    public static CommentNotificationEventDto from(Long receiverId, String nickname, String commentContent) {
        return CommentNotificationEventDto.builder()
                .receiverId(receiverId)
                .nickname(nickname)
                .commentContent(commentContent)
                .build();
    }
}