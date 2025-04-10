package com.team15gijo.search.presentation.controller.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.application.service.v1.SearchService;
import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import com.team15gijo.search.infrastructure.client.user.UserSearchResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
     *  아래 두 메서드 모두 로그인이 구현되면 lastUserId, region를 request에서 받는 걸로 수정할 것
     */

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<CursorResultDto<UserSearchResponseDto>>> searchUser(
            @RequestParam String keyword,
            @RequestParam String region,
            @RequestParam(required = false) Long lastUserId,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success("검색 성공", searchService.searchUsers(keyword, region, lastUserId, size)));
    }


    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<CursorResultDto<PostSearchResponseDto>>> searchPost(
            @RequestParam String keyword,
            @RequestParam String region,
            @RequestParam(required = false) UUID lastPostId,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success("검색 성공", searchService.searchPosts(keyword, region, lastPostId, size)));
    }
}
