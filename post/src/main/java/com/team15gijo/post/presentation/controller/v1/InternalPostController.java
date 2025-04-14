package com.team15gijo.post.presentation.controller.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.post.application.service.v1.InternalPostService;
import com.team15gijo.post.presentation.dto.v1.PostFeedPageResponseDto;
import com.team15gijo.post.presentation.dto.v1.PostSearchResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "게시글 Internal Controller")
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

    /**
     *  게시글 검색
     */
    @GetMapping("/search")
    public ApiResponse<List<PostSearchResponseDto>> searchPosts(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam String region,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "10") int size) {
        log.info("게시글 검색 시작");
        List<PostSearchResponseDto> posts = internalPostService.searchPost(keyword, region, lastCreatedAt, size);
        log.info("게시글 검색 종료");
        return ApiResponse.success("게시글 검색 성공", posts);
    }


}
