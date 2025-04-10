package com.team15gijo.feed.infrastructure.client.post;


import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.feed.presentation.dto.v1.PostFeedPageResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@FeignClient(name = "post-service", contextId = "postClient")
public interface PostClient {
    @GetMapping("/internal/api/v1/posts/recent")
    ApiResponse<PostFeedPageResponseDto> getRecentPostsByRegion(
            @RequestParam("region") String region,
            @RequestParam(value = "cursor", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    );

}
