package com.team15gijo.ai.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    private Long deleteBy; // TODO: 삭제한 시스템 ID (시스템에 의한 자동 삭제는 0L)
    @NotBlank(message = "채팅 메시지 내용은 필수입니다.")
    @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다")
    private String messageContent;

    public static MessageFilteringResponseDto from(boolean isHarmful, String messageContent) {
        return MessageFilteringResponseDto.builder()
            .isHarmful(isHarmful)
            .deleteBy(isHarmful ? 0L : null)
            .messageContent(messageContent)
            .build();
    }
}
