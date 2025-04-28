package com.team15gijo.ai.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageFilteringResponseDto {
    private Boolean isHarmful; // 메시지 제한(삭제) 성공 여부
    private Long deleteBy; // TODO: 삭제한 시스템(임의 관리자 id) -> 0L
    private String messageContent;

    public static MessageFilteringResponseDto from(boolean isHarmful, String messageContent) {
        return MessageFilteringResponseDto.builder()
            .isHarmful(isHarmful)
            .deleteBy(isHarmful ? 0L : null)
            .messageContent(messageContent)
            .build();
    }
}
