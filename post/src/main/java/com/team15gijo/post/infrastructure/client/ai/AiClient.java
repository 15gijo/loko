package com.team15gijo.post.infrastructure.client.ai;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ai-service",url = "${ai.service.url}")
public interface AiClient {

    @PostMapping("/api/v2/hashtags")
    HashtagResponseDto recommendHashtags(HashtagRequestDto request);
}
