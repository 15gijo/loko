package com.team15gijo.comment.infrastructure.client.v2.ai;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ai-service", url = "${ai.service.url}")
public interface ContentModerationClient {
    @PostMapping("/api/v2/moderation")
    ModerationResponseDto moderate(ModerationRequestDto request);
}