package com.team15gijo.chat.infrastructure.client.v2.ai;

import com.team15gijo.chat.application.dto.v1.ChatMessageResponseDto;
import com.team15gijo.chat.infrastructure.client.v2.ai.dto.MessageFilteringResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-service",url = "${ai.service.url}")
public interface AiClient {

    @PostMapping("/api/v2/ai/chats/message/restrict")
    MessageFilteringResponseDto restrict(@RequestBody String messageContent);
}
