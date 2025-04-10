package com.team15gijo.search.application.service.v1;


import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import com.team15gijo.search.infrastructure.client.user.UserSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {
     Page<UserSearchResponseDto> searchUsers(String keyword, Pageable pageable);
    Page<PostSearchResponseDto> searchPosts(String keyword, Pageable pageable);
}
