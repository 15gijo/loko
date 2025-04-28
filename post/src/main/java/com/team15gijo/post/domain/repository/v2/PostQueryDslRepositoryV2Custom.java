package com.team15gijo.post.domain.repository.v2;

import com.team15gijo.post.domain.model.v2.PostV2;
import com.team15gijo.post.domain.repository.v2.dto.PostSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface PostQueryDslRepositoryV2Custom {

    Page<PostSummaryDto> findPostSummaries(Pageable pageable);

    List<PostV2> searchPostsV2(String keyword, String region, LocalDateTime cursor, int size);
}
