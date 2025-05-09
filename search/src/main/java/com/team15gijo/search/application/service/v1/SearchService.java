package com.team15gijo.search.application.service.v1;


import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.application.dto.v2.PostSearchResponseDto;
import com.team15gijo.search.application.dto.v2.UserSearchResponseDto;
import java.time.LocalDateTime;

public interface SearchService {
    CursorResultDto<UserSearchResponseDto> searchUsers(String keyword, Long userId, String nickname, String region, Long lastUserId, int size);
    CursorResultDto<PostSearchResponseDto> searchPosts(String keyword, String region, LocalDateTime lastCreatedAt, int size);
}
