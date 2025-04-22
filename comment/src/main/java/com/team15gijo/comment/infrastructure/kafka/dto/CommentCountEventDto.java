package com.team15gijo.comment.infrastructure.kafka.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentCountEventDto {
    private UUID postId;
    private int  delta;   // +1 or -1
}
