package com.team15gijo.post.domain.repository;

import com.team15gijo.post.domain.model.v1.Post;
import java.time.LocalDateTime;
import java.util.List;

public interface PostQueryDslRepository {
    List<Post> searchPosts(String keyword, String region, LocalDateTime cursor, int size);
}
