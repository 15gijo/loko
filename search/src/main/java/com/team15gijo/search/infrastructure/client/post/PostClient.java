package com.team15gijo.search.infrastructure.client.post;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "post-service")
public interface PostClient {

    @GetMapping("/api/v1/posts/search")
    Page<PostSearchResponseDto> searchPosts(String keyword);
}
