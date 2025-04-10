package com.team15gijo.search.application.service.v1;

import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.infrastructure.client.post.PostClient;
import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import com.team15gijo.search.infrastructure.client.user.UserClient;
import com.team15gijo.search.infrastructure.client.user.UserSearchResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "검색 Service")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final UserClient userClient;
    private final PostClient postClient;


    @Override
    public CursorResultDto<UserSearchResponseDto> searchUsers(
            String keyword,
            String region,
            Long lastUserId,
            int size) {
        List<UserSearchResponseDto> users = userClient.searchUsers(keyword, region, lastUserId, size).getData();
        boolean hasNext = users.size() == size;
        Long nextCursor = hasNext ? users.get(users.size() - 1).getUserId() : null;

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
        List<PostSearchResponseDto> posts = postClient
                .searchPosts(keyword, region, lastCreatedAt, size)
                .getData();

        boolean hasNext = posts.size() == size;
        LocalDateTime nextCursor = hasNext ? posts.get(posts.size() - 1).getCreatedAt() : null;

        return CursorResultDto.<PostSearchResponseDto>builder()
                .items(posts)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }
}
