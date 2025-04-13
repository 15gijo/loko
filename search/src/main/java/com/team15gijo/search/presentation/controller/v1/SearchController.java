package com.team15gijo.search.presentation.controller.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.application.service.v1.SearchService;
import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import com.team15gijo.search.infrastructure.client.user.UserSearchResponseDto;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
@Slf4j(topic = "검색 Controller")
public class SearchController {

    private final SearchService searchService;

    /**
     *  userId와 nickname을 쓸 곳이 있을까?
     */

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<CursorResultDto<UserSearchResponseDto>>> searchUser(
            @RequestParam String keyword,
            @RequestParam(required = false) Long lastUserId,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Nickname") String nickname,
            @RequestHeader("X-User-Region") String encodedRegion) {
        // URL 디코딩하여 원래의 한글 문자열로 복원
        String region = URLDecoder.decode(encodedRegion, StandardCharsets.UTF_8);
        return ResponseEntity.ok(ApiResponse.success("검색 성공", searchService.searchUsers(keyword, region, lastUserId, size)));
    }


    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<CursorResultDto<PostSearchResponseDto>>> searchPost(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "10") int size,
//            @RequestHeader("X-User-Id") Long userId,
//            @RequestHeader("X-User-Nickname") String nickname,
            @RequestHeader("X-User-Region") String encodedRegion) {
        String region = URLDecoder.decode(encodedRegion, StandardCharsets.UTF_8);
        String decodedKeyword = URLDecoder.decode(keyword, StandardCharsets.UTF_8);
        System.out.println(keyword);
        System.out.println(decodedKeyword);
        return ResponseEntity.ok(ApiResponse.success("검색 성공", searchService.searchPosts(keyword, region, lastCreatedAt, size)));
    }
}
