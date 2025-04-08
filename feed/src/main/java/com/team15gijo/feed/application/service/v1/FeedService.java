package com.team15gijo.feed.application.service.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.feed.infrastructure.client.FeignClientService;
import com.team15gijo.feed.infrastructure.client.post.dto.PostFeedPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeignClientService feignClientService;
    /**
     * 동일 지역 정보를 가진 피드 최신순 조회
     * @return f
     */
    public ApiResponse<PostFeedPageResponseDto> getRecentFeedBase(LocalDateTime cursor, int pageSize) {

        // TODO: 요청한 유저의 지역 가져오기 (token or user-service 요청)
        String region = "송파구"; //임시 지역

        ApiResponse<PostFeedPageResponseDto> response = feignClientService.fetchRecentPostsByRegion(region, cursor, pageSize);
        return response;

    }
}
