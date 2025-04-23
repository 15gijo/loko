package com.team15gijo.search.application.service.v1;

import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.infrastructure.client.FeignClientService;
import com.team15gijo.search.application.dto.v2.PostSearchResponseDto;
import com.team15gijo.search.application.dto.v2.UserSearchResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "검색 Service")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {


    private final FeignClientService clientService;


    @Override
    public CursorResultDto<UserSearchResponseDto> searchUsers(
            String keyword,
            Long userId,
            String nickname,
            String region,
            Long lastUserId,
            int size) {
        log.info("유저 검색 client 요청 시작");
        List<UserSearchResponseDto> users = clientService.searchUsers(keyword, userId, nickname, region, lastUserId, size).getData();
        log.info("유저 검색 client 요청 종료");
        boolean hasNext = users.size() == size;
        Long nextCursor = hasNext ? users.get(users.size() - 1).getUserId() : null;
        log.info("유저 검색 종료 전");
        return CursorResultDto.<UserSearchResponseDto>builder()
                .items(users)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }


    @Override
    public CursorResultDto<PostSearchResponseDto> searchPosts(
            String keyword,
            String region,
            LocalDateTime lastCreatedAt,
            int size) {
        log.info("게시글 검색 client 요청 시작");
        List<PostSearchResponseDto> posts = clientService.searchPosts(keyword, region, lastCreatedAt, size)
                .getData();
        log.info("게시글 검색 client 요청 종료");
        boolean hasNext = posts.size() == size;
        LocalDateTime nextCursor = hasNext ? posts.get(posts.size() - 1).getCreatedAt() : null;
        log.info("게시글 검색 종료 전");
        return CursorResultDto.<PostSearchResponseDto>builder()
                .items(posts)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }
}
