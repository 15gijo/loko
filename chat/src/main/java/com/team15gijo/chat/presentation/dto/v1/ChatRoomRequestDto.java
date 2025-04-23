package com.team15gijo.chat.presentation.dto.v1;

import com.team15gijo.chat.domain.model.v1.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomRequestDto {
    private ChatRoomType chatRoomType;
    //TODO: 1:1 수신자(닉네임)으로 유효성 검사
    // 단체채팅 시, List<String> 변경
    private String receiverNickname;
}
