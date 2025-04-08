package com.team15gijo.post.presentation.controller.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.post.application.service.v1.InternalPostService;
import com.team15gijo.post.presentation.dto.v1.PostFeedPageResponseDto;
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
@RequestMapping("/internal/api/v1/posts")
public class InternalPostController {

    private final InternalPostService internalPostService;

    /**
     * 지역별 최신글 조회 (피드)
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<PostFeedPageResponseDto>> getRecentPostsByRegion(
            @RequestParam String region,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        PostFeedPageResponseDto response = internalPostService.getPostsByRegion(region, cursor, pageSize);
        return ResponseEntity.ok(ApiResponse.success("지역별 최신 피드 조회 성공", response));
    }

}
