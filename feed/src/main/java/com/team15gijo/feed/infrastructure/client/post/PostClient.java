package com.team15gijo.feed.infrastructure.client.post;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "post-service", contextId = "postClient")
public interface PostClient {
//    @GetMapping("/internal/")

}
