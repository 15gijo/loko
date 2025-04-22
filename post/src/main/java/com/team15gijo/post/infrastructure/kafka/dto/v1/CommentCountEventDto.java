package com.team15gijo.post.infrastructure.kafka.dto.v1;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentCountEventDto {
    private UUID postId;
    private int  delta;   // +1 or -1
}
