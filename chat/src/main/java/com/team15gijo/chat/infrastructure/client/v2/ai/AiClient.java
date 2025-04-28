package com.team15gijo.chat.infrastructure.client.v2.ai;

import com.team15gijo.chat.infrastructure.client.v2.ai.dto.MessageFilteringResponseDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-service",url = "${ai.service.url}")
public interface AiClient {

    @PostMapping("/api/v2/ai/chats/message/restrict")
    MessageFilteringResponseDto restrict(@Valid @RequestBody String messageContent);
}
