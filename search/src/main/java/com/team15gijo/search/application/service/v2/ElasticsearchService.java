package com.team15gijo.search.application.service.v2;

import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import com.team15gijo.search.infrastructure.kafka.dto.PostElasticsearchRequestDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ElasticsearchService {

    void createElasticPost(PostElasticsearchRequestDto requestDto);

    CursorResultDto<PostSearchResponseDto> searchPost(String keyword, String region, LocalDateTime lastCreatedAt, int size);
}
