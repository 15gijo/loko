package com.team15gijo.search.application.service.v1;

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
    public Page<UserSearchResponseDto> searchUsers(
            String keyword,
            Pageable pageable) {
        return userClient.searchUsers(keyword).getData();
    }


    @Override
    public Page<PostSearchResponseDto> searchPosts(
            String keyword,
            Pageable pageable) {
        return postClient.searchPosts(keyword).getData();
    }
}
