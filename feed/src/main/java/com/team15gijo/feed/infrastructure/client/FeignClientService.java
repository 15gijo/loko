package com.team15gijo.feed.infrastructure.client;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.feed.infrastructure.client.post.PostClient;
import com.team15gijo.feed.infrastructure.client.post.dto.PostFeedPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeignClientService {
    private final PostClient postClient;

    /**
     * Post Service - Feign Client Fetch
     */
    public ApiResponse<PostFeedPageResponseDto> fetchRecentPostsByRegion(String region, LocalDateTime cursor, int pageSize) {
        return postClient.getRecentPostsByRegion(region, cursor, pageSize);
    }



    /**
     * User Service - Feign Client Fetch
     */


}
