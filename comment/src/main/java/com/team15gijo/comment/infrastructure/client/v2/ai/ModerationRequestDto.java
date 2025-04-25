package com.team15gijo.comment.infrastructure.client.v2.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModerationRequestDto {
    private String commentContent;
}
