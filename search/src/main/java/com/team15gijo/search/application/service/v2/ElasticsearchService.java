package com.team15gijo.search.application.service.v2;

import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.application.dto.v2.PostSearchResponseDto;
import com.team15gijo.search.application.dto.v2.UserSearchResponseDto;
import com.team15gijo.search.infrastructure.kafka.dto.v1.PostElasticsearchRequestDto;
import com.team15gijo.search.infrastructure.kafka.dto.v1.UserElasticsearchRequestDto;
import java.time.LocalDateTime;

public interface ElasticsearchService {

    void createElasticPost(PostElasticsearchRequestDto requestDto);

    CursorResultDto<PostSearchResponseDto> searchPost(String keyword, String nickname, String region, LocalDateTime lastCreatedAt, int size);

    void createElasticUser(UserElasticsearchRequestDto dto);

    CursorResultDto<UserSearchResponseDto> searchUser(String keyword, Long userId, String nickname, String region, Long lastUserId, int size);
}
