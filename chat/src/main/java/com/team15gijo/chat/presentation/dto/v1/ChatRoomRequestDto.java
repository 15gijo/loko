package com.team15gijo.chat.presentation.dto.v1;

import com.team15gijo.chat.domain.model.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomRequestDto {
    private ChatRoomType chatRoomType;
    //TODO: 1:1 수신자(닉네임)으로 유효성 검사
    // 단체채팅 시, List<String> 변경
    private String receiver;
}
