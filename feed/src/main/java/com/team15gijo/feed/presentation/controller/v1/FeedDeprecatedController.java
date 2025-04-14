package com.team15gijo.feed.presentation.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team15gijo.common.annotation.RoleGuard;
import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.feed.application.service.v1.FeedDeprecatedService;
import com.team15gijo.feed.presentation.dto.v1.PostFeedPageResponseDto;
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
@RequestMapping("/api/v1/feeds/deprecated")
public class FeedDeprecatedController {

    private final FeedDeprecatedService feedDeprecatedService;

    /**
     * 지역 기반 최신순 피드 조회 - 기본
     * GET /api/v1/feeds/recent/base?cursor=2024-04-07T10:00:00Z&pageSize=10
     */
    @RoleGuard(min = "USER")
    @GetMapping("/recent/base")
    public ResponseEntity<ApiResponse<PostFeedPageResponseDto>> getRecentFeedBase(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "송파구") String region
    ) {
        ApiResponse<PostFeedPageResponseDto> response = feedDeprecatedService.getRecentFeedBase(cursor, pageSize, region);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 지역 기반 최신순 피드 조회 - 캐싱 설정
     */
    @RoleGuard(min = "USER")
    @GetMapping("/recent/cache")
    public ResponseEntity<ApiResponse<PostFeedPageResponseDto>> getRecentFeedCache(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "송파구") String region
    ) throws JsonProcessingException {
        ApiResponse<PostFeedPageResponseDto> response = feedDeprecatedService.getRecentFeedCache(cursor, pageSize, region);
        return ResponseEntity.ok().body(response);
    }

}
