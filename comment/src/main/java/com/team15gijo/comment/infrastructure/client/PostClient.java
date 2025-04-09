package com.team15gijo.comment.infrastructure.client;

import com.team15gijo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "post-service", contextId = "postClient")
public interface PostClient {

    @GetMapping("/api/v1/posts/internal/{postId}/exists")
    ApiResponse<Boolean> exists(@PathVariable("postId") UUID postId);
}
