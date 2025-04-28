package com.team15gijo.chat.infrastructure.client.v2.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageFilteringResponseDto {
    private Boolean isHarmful; // 메시지 제한(삭제) 성공 여부
    private Long deleteBy; // TODO: 삭제한 시스템(임의 관리자 id) -> 0L
    private String messageContent;
}
