package com.team15gijo.search.presentation.controller.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.search.application.service.v1.SearchService;
import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import com.team15gijo.search.infrastructure.client.user.UserSearchResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserSearchResponseDto>>> searchUser(
            @RequestParam String keyword,
            @PageableDefault(size = 10, page = 1, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success("검색 성공", searchService.searchUsers(keyword, pageable)));
    }


    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<PostSearchResponseDto>>> searchPost(
            @RequestParam String keyword,
            @PageableDefault(size = 10, page = 1, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success("검색 성공", searchService.searchPosts(keyword, pageable)));
    }
}
