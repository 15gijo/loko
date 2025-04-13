package com.team15gijo.search.infrastructure.client;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.common.exception.CustomException;
import com.team15gijo.search.domain.exception.SearchDomainExceptionCode;
import com.team15gijo.search.infrastructure.client.post.PostClient;
import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import com.team15gijo.search.infrastructure.client.user.UserClient;
import com.team15gijo.search.infrastructure.client.user.UserSearchResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeignClientService {
    private final UserClient userClient;
    private final PostClient postClient;

    @CircuitBreaker(name = "userClient", fallbackMethod = "searchUsersFallback")
    public ApiResponse<List<UserSearchResponseDto>> searchUsers(
            String keyword,
            Long userId,
            String nickname,
            String region,
            Long lastUserId,
            int size) {
        return userClient.searchUsers(keyword, userId, nickname, region, lastUserId, size);
    }

    public ApiResponse<List<UserSearchResponseDto>> searchUsersFallback(
            String keyword,
            Long userId,
            String nickname,
            String region,
            Long lastUserId,
            int size,
            Throwable t) {
        log.error("User Search Feign Client 호출 실패 (Fallback 처리): {}", t.getMessage());
        throw new CustomException(SearchDomainExceptionCode.USER_SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "postClient", fallbackMethod = "searchPostsFallback")
    public ApiResponse<List<PostSearchResponseDto>> searchPosts(
            String keyword,
            String region,
            LocalDateTime lastCreatedAt,
            int size) {
        return postClient.searchPosts(keyword, region, lastCreatedAt, size);
    }

    public ApiResponse<List<UserSearchResponseDto>> searchPostsFallback(
            String keyword,
            String region,
            LocalDateTime lastCreatedAt,
            int size,
            Throwable t) {
        log.error("Post Search Feign Client 호출 실패 (Fallback 처리): {}", t.getMessage());
        throw new CustomException(SearchDomainExceptionCode.POST_SERVICE_UNAVAILABLE);
    }

}
