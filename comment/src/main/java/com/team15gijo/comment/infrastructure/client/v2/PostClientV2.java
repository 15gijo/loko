package com.team15gijo.comment.infrastructure.client.v2;

import com.team15gijo.common.dto.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "post-service", contextId = "postClient")
public interface PostClientV2 {

    @GetMapping("/api/v1/posts/{postId}/exists")
    ApiResponse<Boolean> exists(@PathVariable("postId") UUID postId);


    @PostMapping("/api/v1/posts/{postId}/increment-comment")
    ApiResponse<Void> addCommentCount(@PathVariable("postId") UUID postId);

    @PostMapping("/api/v1/posts/{postId}/decrement-comment")
    ApiResponse<Void> decreaseCommentCount(@PathVariable("postId") UUID postId);

}
