package com.team15gijo.search.domain.service;

import com.team15gijo.search.application.dto.v1.SearchResponseDto;
import com.team15gijo.search.application.service.v1.SearchService;
import com.team15gijo.search.infrastructure.client.post.PostClient;
import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import com.team15gijo.search.infrastructure.client.user.UserClient;
import com.team15gijo.search.infrastructure.client.user.UserSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public SearchResponseDto searchUsersAndPosts(
            String keyword,
            Pageable pageable) {
        Page<UserSearchResponseDto> users = userClient.searchUsers(keyword);
        Page<PostSearchResponseDto> posts = postClient.searchPosts(keyword);
        return SearchResponseDto.builder()
                .users(users)
                .posts(posts)
                .build();
    }
}
