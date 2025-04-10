package com.team15gijo.search.infrastructure.client.post;

import com.team15gijo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "post-service")
public interface PostClient {

    @GetMapping("/api/v1/posts/search")
    ApiResponse<Page<PostSearchResponseDto>> searchPosts(String keyword);
}
