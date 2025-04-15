package com.team15gijo.post.domain.repository;


import com.team15gijo.post.domain.model.v2.Post;
import com.team15gijo.post.domain.model.v2.PostLike;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {
    Optional<PostLike> findByPostAndUserId(Post post, long userId);
    List<PostLike> findByPost(Post post);
    List<PostLike> findByUserId(long userId);
}
