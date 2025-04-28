package com.team15gijo.search.presentation.controller.v2;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.application.service.v2.ElasticsearchService;
import com.team15gijo.search.application.dto.v2.PostSearchResponseDto;
import com.team15gijo.search.application.dto.v2.UserSearchResponseDto;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/search")
@Slf4j
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;

    @GetMapping("/post")
    public ResponseEntity<ApiResponse<CursorResultDto<PostSearchResponseDto>>> searchPost(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-User-Nickname") String encodedNickname,
            @RequestHeader("X-User-Region") String encodedRegion) {
        log.info("게시글 검색 시작");
        String region = URLDecoder.decode(encodedRegion, StandardCharsets.UTF_8);
        String nickname = URLDecoder.decode(encodedNickname, StandardCharsets.UTF_8);
        log.info("nickname : {},  region : {}, size : {}, keyword : {}, lastCreatedAt : {}", nickname, region, size, keyword, lastCreatedAt);
        return ResponseEntity.ok(ApiResponse.success("게시글 검색 성공", elasticsearchService.searchPost(keyword, nickname, region, lastCreatedAt, size)));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<CursorResultDto<UserSearchResponseDto>>> searchUser(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(required = false) Long lastUserId,
            @RequestParam(defaultValue = "5") int size,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Nickname") String encodedNickname,
            @RequestHeader("X-User-Region") String encodedRegion) {
        log.info("유저 검색 시작");
        String region = URLDecoder.decode(encodedRegion, StandardCharsets.UTF_8);
        String nickname = URLDecoder.decode(encodedNickname, StandardCharsets.UTF_8);
        log.info("region : {}, nickname: {}, size : {}, keyword : {}, lastUserId : {}", region, nickname, size, keyword, lastUserId);
        return ResponseEntity.ok(ApiResponse.success("유저 검색 성공", elasticsearchService.searchUser(keyword, userId, region, lastUserId, size)));
    }

}
