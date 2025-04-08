package com.team15gijo.feed.presentation.controller.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.feed.application.service.v1.FeedService;
import com.team15gijo.feed.infrastructure.client.post.dto.PostFeedPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {

    private final FeedService feedService;

    /**
     * 지역 기반 최신순 피드 조회 - 기본
     * GET /api/v1/feeds/recent/base?cursor=2024-04-07T10:00:00Z&pageSize=10
     */
    @GetMapping("/recent/base")
    public ResponseEntity<ApiResponse<PostFeedPageResponseDto>> getRecentFeedBase(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiResponse<PostFeedPageResponseDto> response = feedService.getRecentFeedBase(cursor, pageSize);
        return ResponseEntity.ok().body(response);
    }



    /**
     * 지역 기반 최신순 피드 조회 - 지역 인덱싱
     */


    /**
     * 지역 기반 최신순 피드 조회 - 캐싱 설정
     */



}
