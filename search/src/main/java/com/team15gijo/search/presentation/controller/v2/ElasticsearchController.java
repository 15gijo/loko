package com.team15gijo.search.presentation.controller.v2;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.application.service.v2.ElasticsearchService;
import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/search")
@Slf4j(topic = "검색 Controller")
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;

//    @PostMapping("/post")
//    public ResponseEntity<ApiResponse<String>> createElasticPost(@RequestBody PostSearchResponseDto responseDto) {
//        return ResponseEntity.ok(ApiResponse.success("게시글 저장 성공", elasticsearchService.createElasticPost(responseDto)));
//    }

    @GetMapping("/post")
    public ResponseEntity<ApiResponse<CursorResultDto<PostSearchResponseDto>>> searchPost(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-User-Region") String encodedRegion) {
        log.info("게시글 검색 시작");
        String region = URLDecoder.decode(encodedRegion, StandardCharsets.UTF_8);
        log.info("region : {}, size : {}, keyword : {}, lastCreatedAt : {}", region, size, keyword, lastCreatedAt);
        return ResponseEntity.ok(ApiResponse.success("게시글 저장 성공", elasticsearchService.searchPost(keyword, region, lastCreatedAt, size)));
    }

}
