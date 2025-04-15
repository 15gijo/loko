package com.team15gijo.post.domain.repository.v2;

import com.team15gijo.post.domain.model.v2.PostV2;
import java.time.LocalDateTime;
import java.util.List;

public interface PostQueryDslRepositoryV2 {
    List<PostV2> searchPostsV2(String keyword, String region, LocalDateTime cursor, int size);
}
