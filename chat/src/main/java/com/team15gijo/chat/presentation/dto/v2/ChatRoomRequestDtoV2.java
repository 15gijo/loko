package com.team15gijo.chat.presentation.dto.v2;

import com.team15gijo.chat.domain.model.v2.ChatRoomTypeV2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomRequestDtoV2 {
    private ChatRoomTypeV2 chatRoomType;
    //TODO: 1:1 수신자(닉네임)으로 유효성 검사
    // 단체채팅 시, List<String> 변경
    private String receiverNickname;
}
