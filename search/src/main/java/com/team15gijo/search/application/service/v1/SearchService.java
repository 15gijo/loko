package com.team15gijo.search.application.service.v1;


import com.team15gijo.search.application.dto.v1.SearchResponseDto;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    SearchResponseDto searchUsersAndPosts(String keyword, Pageable pageable);
}
