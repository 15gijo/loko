package com.team15gijo.search.infrastructure.client.post;

import com.team15gijo.common.dto.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "post-service")
public interface PostClient {

    @GetMapping("/internal/api/v1/posts/search")
    ApiResponse<List<PostSearchResponseDto>> searchPosts(
            @RequestParam String keyword,
            @RequestParam String region,
            @RequestParam UUID lastPostId,
            @RequestParam int size);
}
