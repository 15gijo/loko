package com.team15gijo.search.application.service.v2;

import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import java.util.List;

public interface ElasticsearchService {

    String createElasticPost(PostSearchResponseDto responseDto);

    List<PostSearchResponseDto> searchPost(String keyword, String region);
}
