package com.team15gijo.search.application.service.v2;

import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import com.team15gijo.search.infrastructure.client.user.UserSearchResponseDto;
import com.team15gijo.search.infrastructure.kafka.dto.PostElasticsearchRequestDto;
import com.team15gijo.search.infrastructure.kafka.dto.UserElasticsearchRequestDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ElasticsearchService {

    void createElasticPost(PostElasticsearchRequestDto requestDto);

    CursorResultDto<PostSearchResponseDto> searchPost(String keyword, String region, LocalDateTime lastCreatedAt, int size);

    void createElasticUser(UserElasticsearchRequestDto dto);

    CursorResultDto<UserSearchResponseDto> searchUser(String keyword, Long userId, String nickname, String region, Long lastUserId, int size);
}
