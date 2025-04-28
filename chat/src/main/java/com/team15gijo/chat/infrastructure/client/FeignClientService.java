package com.team15gijo.chat.infrastructure.client;

import com.team15gijo.chat.infrastructure.client.v1.user.UserClient;
import com.team15gijo.chat.infrastructure.client.v2.ai.AiClient;
import com.team15gijo.chat.infrastructure.client.v2.ai.dto.MessageFilteringResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeignClientService {

    private final UserClient userClient;
    private final AiClient aiClient;

    /**
     * User 서비스에서 nickname 존재여부 확인 - Feign Client
     */
    public Long fetchUserIdByNickname(String receiverNickname) {
        return userClient.getUserIdByNickname(receiverNickname);
    }

    /**
     * Ai 서비스에서 채팅 메시지 내용 필터링에 적용 - Feign client
     */
    public MessageFilteringResponseDto fetchIsHarmfulByMessage(String messageContent) {
        return aiClient.restrict(messageContent);
    }
}
